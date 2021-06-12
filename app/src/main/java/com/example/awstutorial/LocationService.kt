package com.example.awstutorial

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest : LocationRequest
    private var lastLocation: Location? = null
    fun getLocation() : Location? {
        return lastLocation
    }
    fun requestLocationUpdates() {
        fun hasPermissions() : Boolean = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )
        val looper = Looper.myLooper()
        looper ?: return //TODO what if looper is null
        if (hasPermissions()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper)
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                lastLocation = location
            }
        }
        else
        {
            //TODO request permissions
        }
}



    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) ==  ConnectionResult.SUCCESS) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        } else {
            Log.e(LocationService.TAG, "Cant start Google Play API")
            //Toast.makeText(this, "Cant start Google Play API", Toast.LENGTH_SHORT).show()
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)  //??? added
                lastLocation = locationResult.lastLocation
                //for (location in locationResult.locations){ //Alternatively, we can iterate, through locations:
                //    // Update UI with location data
                //    // ...
                //}
                //displayLocation(location, locationResult.locations.size)
            }
        }
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }


    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i(TAG, "onBind")
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    companion object {
        private const val TAG = "LocationService"
    }
}