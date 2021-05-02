package com.example.awstutorial

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.DeadObjectException
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class LocationServiceManager(private val activity: ComponentActivity) {

    private lateinit var locationService : LocationService
    private var locationServiceBound : Boolean = false
    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            locationServiceBound = true
            Log.i(TAG, "onServiceConnected")
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            locationServiceBound = false
            Log.i(TAG, "onServiceConnected")
        }
    }
    private val requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            requestServiceToRequestLocationUpdates()
        } else {
            onPermissionsExplicitlyRejected?.informUser()  // ?: TODO log assertion
        }
    }
    private var onPermissionsExplicitlyRejected: OnPermissionsExplicitlyRejected? = null
    inner class OnPermissionsExplicitlyRejected(private val contentViewOfActivity: View) {
        fun informUser() {
            Snackbar.make(
                    contentViewOfActivity,
                    "You disabled location, some features won't be available", //TODO text resource
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, View.OnClickListener {})
                    .show()
        }
    }

    fun bind() {
        Intent(activity, LocationService::class.java).also { intend ->
            activity.bindService(intend, locationServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbind() {
        activity.unbindService(locationServiceConnection)
        locationServiceBound = false
    }

    fun requestLocationUpdates(contentViewOfActivity : View) {
        when {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                requestServiceToRequestLocationUpdates()
            }
            activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                Snackbar.make(
                        contentViewOfActivity,
                        "Hey, please enable location", //TODO: text resource
                        Snackbar.LENGTH_LONG)
                        .setAction("Enable", View.OnClickListener { //TODO: text resource
                            onPermissionsExplicitlyRejected = OnPermissionsExplicitlyRejected(contentViewOfActivity)
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        })
                        .show()

            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                onPermissionsExplicitlyRejected = OnPermissionsExplicitlyRejected(contentViewOfActivity)
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun requestServiceToRequestLocationUpdates() {
        if (!locationServiceBound) {
            Log.e(TAG, "requestServiceToRequestLocationUpdates() locationServiceBound==false")
            return
        }
        try {
            locationService.requestLocationUpdates()
        }
        catch (e: UninitializedPropertyAccessException) //locationService not initialised yet
        {
            Log.e(TAG, "requestServiceToRequestLocationUpdates() UninitializedPropertyAccessException")
        }
        catch (e: DeadObjectException) //locationService method cant be called, because locationService not bound yet
        {
            Log.e(TAG, "requestServiceToRequestLocationUpdates() DeadObjectException")
        }
    }


    fun getLastLocation(): Location? {
        if (!locationServiceBound) {
            Log.e(TAG, "getLastLocation() locationServiceBound==false")
            return null
        }
        return try {
            locationService.getLocation()
        }
        catch (e: UninitializedPropertyAccessException) //locationService not initialised yet
        {
            Log.e(TAG, "getLastLocation() UninitializedPropertyAccessException")
            return null
        }
        catch (e: DeadObjectException) //locationService method cant be called, because locationService not bound yet
        {
            Log.e(TAG, "getLastLocation() DeadObjectException")
            return null
        }
    }
    companion object {
        private const val TAG = "LocationServiceManager"
    }
}