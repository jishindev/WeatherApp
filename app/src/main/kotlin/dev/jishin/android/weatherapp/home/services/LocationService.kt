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
    private lateinit var serviceHandler: Handler
    private var isChangingConfig = false


    @SuppressLint("NewApi")
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult ?: return
                onNewLocation(locationResult.lastLocation)
            }
        }

        val handlerThread = HandlerThread(javaClass.simpleName)
        handlerThread.start()
        serviceHandler = Handler(handlerThread.looper)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("onStartCommand, intent: $intent, flags: $flags, startId: $startId")

        // do not recreate after getting killed
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        isChangingConfig = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        Timber.i("onBind, intent: $intent")
        stopForeground(true)
        isChangingConfig = false
        return binder
    }

    override fun onRebind(intent: Intent?) {
        Timber.i("onRebind, intent: $intent")
        stopForeground(true)
        isChangingConfig = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.i("onUnbind, intent: $intent")
        stopLocationUpdates()
        return true
    }

    override fun onDestroy() {
        stopLocationUpdates()
        serviceHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun onNewLocation(location: Location?) {
        Timber.i("onNewLocation, location: $location")
        location ?: return
        this.location = location
        broadcastLocation(location)
    }

    private fun broadcastLocation(location: Location?) {
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

        isRequestingLocationUpdates = true
        startService(Intent(applicationContext, LocationService::class.java))
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            e.printStackTrace()
            isRequestingLocationUpdates = false
        }
    }

    private fun stopLocationUpdates() {
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
        if (isLocationPermsGranted()) {
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    location = task.result
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
        const val KEY_LOCATION = "Location"
        const val ACTION_BROADCAST = "RoadRunnerBroadcast"

        val locationRequest by lazy {
            LocationRequest().apply {
                interval = 3200
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }
    }
}