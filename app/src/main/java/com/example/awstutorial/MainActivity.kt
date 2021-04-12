package com.example.awstutorial

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.core.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationGetsCounter: UInt = 0u;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        // prepare our List view and RecyclerView (cells)
        setupRecyclerViewNotes(note_list)
        setupRecyclerViewItems(item_list)

        setupAuthButton(UserData)

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) ==  ConnectionResult.SUCCESS) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        } else {
            Log.e(TAG, "Cant start Google Play API")
            Toast.makeText(this, "Cant start Google Play API", Toast.LENGTH_SHORT).show()
        }
        UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
            // update UI
            Log.i(TAG, "isSignedIn changed : $isSignedUp")

            //animation inspired by https://www.11zon.com/zon/android/multiple-floating-action-button-android.php
            if (isSignedUp) {
                fabAuth.setImageResource(R.drawable.ic_baseline_lock_open)
                Log.d(TAG, "Showing fabADD")
                fabAdd.show()
                fabItemAdd.show()
            } else {
                fabAuth.setImageResource(R.drawable.ic_baseline_lock)
                Log.d(TAG, "Hiding fabADD")
                fabAdd.hide()
                fabItemAdd.hide()
            }
        })

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        fabItemAdd.setOnClickListener {
            startActivity(Intent(this, AddActivityItem::class.java))
        }

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a lateinit var in your onAttach() or onCreate() method.
        val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        val locationRequest = LocationRequest.create().apply {
                            interval = 10000
                            fastestInterval = 5000
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        }
                        fusedLocationClient.requestLocationUpdates(locationRequest,
                                locationCallback,
                                Looper.getMainLooper())
                        //fusedLocationClient.lastLocation
                        //        .addOnSuccessListener { location : Location? ->
                        //            // Got last known location. In some rare situations this can be null.
                        //            if (location != null) {
                        //                val latitude = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES)
                        //                val longitude = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES)
                        //                locationTextView.setText("N:$latitude, E:$longitude")
                        //            }
                        //        }
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }
                }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                locationGetsCounter += 1u
                val location = locationResult.lastLocation
                //for (location in locationResult.locations){ //Alternatively, we can iterate, through locations:
                //    // Update UI with location data
                //    // ...
                //}
                displayLocation(location, locationResult.locations.size)
            }
        }

        fabGetLocation.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    val locationRequest = LocationRequest.create().apply {
                        interval = 10000
                        fastestInterval = 5000
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest,
                            locationCallback,
                            Looper.getMainLooper())
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.

                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    private fun displayLocation(location: Location, numOfLocations: Int)
    {
        val latitude = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS)
        val longitude = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS)
        locationTextView.setText("N:$latitude, E:$longitude, Locs:${numOfLocations}, Cnt:${locationGetsCounter}")
    }

    // recycler view is the list of cells
    private fun setupRecyclerViewNotes(recyclerView: RecyclerView) {
        // update individual cell when the Note data are modified
        UserData.notes().observe(this, Observer<MutableList<UserData.Note>> { notes ->
            Log.d(TAG, "Note observer received ${notes.size} notes")

            // let's create a RecyclerViewAdapter that manages the individual cells
            recyclerView.adapter = NoteRecyclerViewAdapter(notes)
        })
        // add a touch gesture handler to manager the swipe to delete gesture
        val noteSwipeCallback = object : SwipeCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                Toast.makeText(activity, "deleted", Toast.LENGTH_SHORT).show()

                //Remove swiped item from list and notify the RecyclerView
                Log.d(TAG, "Going to remove ${viewHolder.adapterPosition}")

                // get the position of the swiped item in the list
                val position = viewHolder.adapterPosition

                // remove to note from the userdata will refresh the UI
                val note = UserData.deleteNote(position)

                // async remove from backend
                Backend.deleteNote(note)
            }
        }
        val itemTouchHelper = ItemTouchHelper(noteSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    // recycler view is the list of cells
    private fun setupRecyclerViewItems(recyclerView: RecyclerView) {
        // update individual cell when the Item data are modified
        UserData.items().observe(this, Observer<MutableList<UserData.Item>> { items ->
            Log.d(TAG, "Item observer received ${items.size} notes")

            // let's create a RecyclerViewAdapter that manages the individual cells
            recyclerView.adapter = ItemRecyclerViewAdapter(items)
        })



        // add a touch gesture handler to manager the swipe to delete gesture
        val itemSwipeCallback = object : SwipeCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                Toast.makeText(activity, "deleted", Toast.LENGTH_SHORT).show()

                //Remove swiped item from list and notify the RecyclerView
                Log.d(TAG, "Going to remove ${viewHolder.adapterPosition}")

                // get the position of the swiped item in the list
                val position = viewHolder.adapterPosition

                // remove to note from the userdata will refresh the UI
                val item = UserData.deleteItem(position)

                // async remove from backend
                Backend.deleteItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun setupAuthButton(userData: UserData) {

        // register a click listener
        fabAuth.setOnClickListener { view ->

            val authButton = view as FloatingActionButton

            if (userData.isSignedIn.value!!) {
                authButton.setImageResource(R.drawable.ic_baseline_lock_open)
                Backend.signOut()
            } else {
                authButton.setImageResource(R.drawable.ic_baseline_lock_open)
                Backend.signIn(this)
            }
        }
    }
    // receive the web redirect after authentication
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Backend.handleWebUISignInResponse(requestCode, resultCode, data)
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}