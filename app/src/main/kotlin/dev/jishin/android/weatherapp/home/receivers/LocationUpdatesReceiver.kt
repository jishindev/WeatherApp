package dev.jishin.android.weatherapp.home.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import dev.jishin.android.weatherapp.home.services.LocationService
import timber.log.Timber

class LocationUpdatesReceiver(private val onNewLocation: (Location) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val location = intent?.getParcelableExtra<Location>(LocationService.KEY_LOCATION)
        Timber.i("onReceive() called with: location = [$location]")
        location?.let { onNewLocation(it) }
    }
}