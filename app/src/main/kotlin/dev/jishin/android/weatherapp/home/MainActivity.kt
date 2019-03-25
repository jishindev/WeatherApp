package dev.jishin.android.weatherapp.home

import android.annotation.SuppressLint
import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.android.AndroidInjection
import dev.jishin.android.weatherapp.R
import dev.jishin.android.weatherapp.home.adapters.RvForecastAdapter
import dev.jishin.android.weatherapp.home.receivers.LocationUpdatesReceiver
import dev.jishin.android.weatherapp.home.services.LocationService
import dev.jishin.android.weatherapp.home.services.LocationService.Companion.locationRequest
import dev.jishin.android.weatherapp.network.models.WeatherResponse
import dev.jishin.android.weatherapp.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import ru.gildor.coroutines.retrofit.Result
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val mainVM by lazy { getVM<MainVM>(vmFactory) }

    private var locationService: LocationService? = null
    private val locationReceiver = LocationUpdatesReceiver {
        loadForecast(it)
        locationService?.stopLocationUpdates()
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

        initViews()
    }

    private fun initViews() {
        btnRetry.setOnClickListener {
            startLocationUpdates()
        }

        rvForecast.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val divider = DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(divider)
            setHasFixedSize(true)
            adapter = RvForecastAdapter()
            translationY = 1500f
        }
    }

    override fun onStart() {
        super.onStart()

        bindLocationService()
        showProgress(true)
    }

    override fun onResume() {
        super.onResume()

        // Register location receiver
        localBroadcastManager.registerReceiver(
            locationReceiver,
            IntentFilter(LocationService.ACTION_BROADCAST)
        )
    }

    private fun showProgress(show: Boolean) {
        Timber.i("showProgress called, show: $show")

        clRoot.setBackgroundColor(getColorVal(R.color.rootBg))
        if (show) {
            ivProgress.show()
            val rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
            rotateAnim.interpolator = LinearInterpolator()
            ivProgress.startAnimation(rotateAnim)
        } else {
            ivProgress.clearAnimation()
            ivProgress.hide()
        }
    }

    private fun showError(show: Boolean) {
        Timber.i("showError() called, show: $show")

        if (show) {
            tvError.show()
            btnRetry.show()
            clRoot.setBackgroundColor(getColorVal(R.color.errorBg))
        } else {
            tvError.hide()
            btnRetry.hide()
            clRoot.setBackgroundColor(getColorVal(R.color.rootBg))
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun showForecast(show: Boolean, weatherResponse: WeatherResponse? = null) {
        Timber.i("showForecast() called, show: $show, weatherResponse: $weatherResponse")

        clRoot.setBackgroundColor(getColorVal(R.color.rootBg))

        if (show) {
            tvCurrentTemp.show()
            tvCity.show()
            rvForecast.show()
        } else {
            tvCurrentTemp.hide()
            tvCity.hide()
            rvForecast.hide()
        }

        weatherResponse?.let { weather ->
            tvCurrentTemp.text = "${Math.round(weather.current.tempC ?: 0f)}\u00B0"
            tvCity.text = weather.location.name
            weather.forecast.forecastDay?.let {
                (rvForecast.adapter as RvForecastAdapter).updateForecastItems(it)
                rvForecast.animate().apply {
                    translationY(0f)
                    duration = 1000
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }
        }
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
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showError(true)
                            }
                        }
                    }
                }
        }
    }

    private fun startLocationUpdates() {
        Timber.i("startLocationUpdates called")

        ifLocationSettingsSatisfied {
            // Check again in case if the
            ifLocationPermGranted {
                locationService?.startLocationUpdates()
            }
        }
    }

    private fun loadForecast(location: Location) {
        Timber.i("loadForecast() called with: location = [$location]")

        if (isConnected()) {
            mainVM.getForecast(this@MainActivity, location).observe(this@MainActivity, Observer {
                Timber.i("loadForecast() observe called: weatherResponse: $it")

                when (it) {
                    is Result.Ok -> {
                        showForecast(true, it.value)
                        showError(false)
                    }
                    is Result.Error -> {
                        showForecast(false)
                        showError(true)
                    }
                    is Result.Exception -> {
                        showForecast(false)
                        showError(true)
                    }
                }

                showProgress(false)
            })
        } else {
            Timber.e("loadForecast, not connected to internet")
            showError(true)
        }
    }

    override fun onPause() {
        super.onPause()
        localBroadcastManager.unregisterReceiver(locationReceiver)
    }

    override fun onStop() {
        unbindLocationService()
        super.onStop()
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_LOCATION_SETTINGS -> startLocationUpdates()
        }
    }

    private fun bindLocationService() {
        Timber.i("bindLocationService called")
        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindLocationService() {
        Timber.i("unbindLocationService called")
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
