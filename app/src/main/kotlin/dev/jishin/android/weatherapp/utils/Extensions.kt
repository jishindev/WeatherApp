package dev.jishin.android.weatherapp.utils

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import dev.jishin.android.weatherapp.App

inline fun <reified VM : ViewModel> FragmentActivity.getVM(
    viewModelFactory: ViewModelProvider.Factory
) = ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)

fun App.registerActivityCreatedCallbacks(activityCreated: (Activity) -> Unit) {

    registerActivityLifecycleCallbacks(object :
        Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {}

        override fun onActivityResumed(activity: Activity?) {}

        override fun onActivityStarted(activity: Activity?) {}

        override fun onActivityDestroyed(activity: Activity?) {}

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

        override fun onActivityStopped(activity: Activity?) {}

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            activity?.let { activityCreated(it) }
        }
    })
}

fun Context.isLocationPermsGranted() = ActivityCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED


fun Activity.ifLocationPermGranted(onDenied: () -> Unit = {}, onAccepted: () -> Unit) {

    val permBuilder = permissionsBuilder(Manifest.permission.ACCESS_FINE_LOCATION).build()
    permBuilder.listeners {
        onAccepted {
            onAccepted()
        }

        onDenied {
            onDenied()
        }
    }
    permBuilder.send()
}

fun Context.isConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val networkInfo = connectivityManager?.activeNetworkInfo
    return networkInfo?.isConnected ?: false
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide(makeGone: Boolean = true) {
    visibility = if (makeGone) View.GONE else View.INVISIBLE
}

fun Context.getColorVal(@ColorRes colorResId: Int) =
    ContextCompat.getColor(this, colorResId)