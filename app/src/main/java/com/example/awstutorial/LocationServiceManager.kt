package com.example.awstutorial

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.DeadObjectException
import android.os.IBinder

class LocationServiceManager {

    private lateinit var locationService : LocationService
    private var locationServiceBound : Boolean = false
    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            locationServiceBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            locationServiceBound = false
        }
    }

    fun bind(context: Context ) {
        Intent(context, LocationService::class.java).also { intend ->
            context.bindService(intend, locationServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbind(context: Context) {
        context.unbindService(locationServiceConnection)
        locationServiceBound = false
    }

    fun requestLocationUpdates() {
        if (!locationServiceBound) {
            return
        }
        try {
            locationService.requestLocationUpdates()
        }
        catch (e: UninitializedPropertyAccessException) //locationService not initialised yet
        {
        }
        catch (e: DeadObjectException) //locationService method cant be called, because locationService not bound yet
        {
        }
    }


    fun getLastLocation(): Location? {
        if (!locationServiceBound) {
            return null
        }
        return try {
            locationService.getLocation()
        }
        catch (e: UninitializedPropertyAccessException) //locationService not initialised yet
        {
            return null
        }
        catch (e: DeadObjectException) //locationService method cant be called, because locationService not bound yet
        {
            return null
        }
    }
}