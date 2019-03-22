package dev.jishin.android.weatherapp.home

import android.annotation.SuppressLint
import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.android.AndroidInjection
import dev.jishin.android.weatherapp.R
import dev.jishin.android.weatherapp.home.receivers.LocationUpdatesReceiver
import dev.jishin.android.weatherapp.home.services.LocationService
import dev.jishin.android.weatherapp.home.services.LocationService.Companion.locationRequest
import dev.jishin.android.weatherapp.utils.getVM
import dev.jishin.android.weatherapp.utils.ifLocationPermGranted
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val mainVM by lazy { getVM<MainVM>(vmFactory) }

    private var locationService: LocationService? = null
    private val locationReceiver = LocationUpdatesReceiver {
        loadUserCity(it)
    }
    private var isBound = false
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName, iBinder: IBinder) {
            Timber.i("onServiceConnected, p0: $p0, p1: $iBinder")
            locationService = (iBinder as LocationService.LocalBinder?)?.service
            isBound = true

            startLocationUpdates()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            locationService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        bindLocationService()
    }

    override fun onResume() {
        super.onResume()

        // Register location receiver
        localBroadcastManager.registerReceiver(
            locationReceiver,
            IntentFilter(LocationService.ACTION_BROADCAST)
        )
    }

    private fun ifLocationSettingsSatisfied(onSettingsSatisfied: () -> Unit) {
        Timber.i("ifLocationSettingsSatisfied() called")

        ifLocationPermGranted {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .apply {
                    addOnSuccessListener { onSettingsSatisfied() }
                    addOnFailureListener { e ->
                        if (e is ResolvableApiException) {
                            try {
                                e.startResolutionForResult(this@MainActivity, RC_LOCATION_SETTINGS)
                            } catch (e: IntentSender.SendIntentException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
        }
    }

    private fun startLocationUpdates() {
        Timber.i("startLocationUpdates() called")

        ifLocationSettingsSatisfied {
            // Check again in case if the
            ifLocationPermGranted {
                locationService?.startLocationUpdates()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun loadUserCity(location: Location) {
        Timber.i("loadUserCity() called with: location = [$location]")


        mainVM.getUserCity(location).observe(this@MainActivity, Observer { city ->
            Timber.i("loadUserCity() observe called: city: $city")

            city?.let { loc ->
                Timber.d("loadUserCity: city: $loc")
            }
        })

    }

    override fun onPause() {
        super.onPause()
        localBroadcastManager.unregisterReceiver(locationReceiver)
    }

    override fun onStop() {
        unbindLocationService()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_LOCATION_SETTINGS -> startLocationUpdates()
        }
    }

    private fun bindLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindLocationService() {
        if (isBound) {
            try {
                unbindService(serviceConnection)
                isBound = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val RC_LOCATION_SETTINGS = 1001
    }
}
