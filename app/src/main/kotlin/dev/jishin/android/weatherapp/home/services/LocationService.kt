package dev.jishin.android.weatherapp.home.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.android.AndroidInjection
import dev.jishin.android.weatherapp.utils.isLocationPermsGranted
import timber.log.Timber
import javax.inject.Inject

class LocationService : Service() {

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val binder = LocalBinder()
    private lateinit var locationCallback: LocationCallback
    private var location: Location? = null

    @SuppressLint("NewApi")
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                Timber.i("onLocationResult, locationResult: $locationResult")
                locationResult ?: return
                onNewLocation(locationResult.lastLocation)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("onStartCommand, intent: $intent, flags: $flags, startId: $startId")

        // do not recreate after getting killed
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Timber.i("onBind, intent: $intent")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.i("onUnbind, intent: $intent")
        stopLocationUpdates()
        return true
    }

    override fun onDestroy() {
        Timber.i("onDestroy")
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun onNewLocation(location: Location?) {
        Timber.i("onNewLocation, location: $location")
        location ?: return
        this.location = location
        broadcastLocation(location)
    }

    private fun broadcastLocation(location: Location?) {
        Timber.i("broadcastLocation, location: $location")
        location ?: return

        Intent(ACTION_BROADCAST).apply {
            putExtra(KEY_LOCATION, location)
        }.also {
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(it)
        }
    }

    private var isRequestingLocationUpdates: Boolean = true

    fun startLocationUpdates() {
        Timber.i("startLocationUpdates() called")
        getLastLocation()
        isRequestingLocationUpdates = true
        startService(Intent(applicationContext, LocationService::class.java))
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            e.printStackTrace()
            isRequestingLocationUpdates = false
        }
    }

    fun stopLocationUpdates() {
        Timber.i("stopLocationUpdates() called")
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isRequestingLocationUpdates = false
            stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
            isRequestingLocationUpdates = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Timber.i("getLastLocation()")
        if (isLocationPermsGranted()) {
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    location = task.result
                    onNewLocation(location)
                } else {
                    Timber.e("error getLastLocation, task: $task")
                }
            }
        }
    }

    inner class LocalBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }

    companion object {
        const val KEY_LOCATION = "dev.jishin.android.weatherapp.home.services.Location"
        const val ACTION_BROADCAST = "dev.jishin.android.weatherapp.home.services.LocationBroadcast"

        val locationRequest by lazy {
            LocationRequest().apply {
                interval = 3200
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }
    }
}