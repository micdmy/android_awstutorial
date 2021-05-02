package com.example.awstutorial

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
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

    private var locationGetsCounter: UInt = 0u;
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var locationServiceManager: LocationServiceManager


    override fun onStart() {
        super.onStart()
        locationServiceManager = LocationServiceManager(this)
        locationServiceManager.bind()
    }

    override fun onStop() {
        super.onStop()
        locationServiceManager.unbind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        // prepare our List view and RecyclerView (cells)
        setupRecyclerViewNotes(note_list)
        setupRecyclerViewItems(item_list)

        setupAuthButton(UserData)

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
            val intend = Intent(this, AddActivityItem::class.java)
            val bundle = Bundle()
            bundle.putDouble("latitude", latitude)
            bundle.putDouble("longitude", longitude)
            intend.putExtras(bundle)
            startActivity(intend)
        }

        fabGetLocation.setOnClickListener {
            locationServiceManager.requestLocationUpdates(findViewById(android.R.id.content))
        }

        locationTextView.setOnClickListener {
            displayLocation(locationServiceManager.getLastLocation(), 123)
        }
    }

    private fun displayLocation(location: Location?, numOfLocations: Int)
    {
        if (location == null)
        {
            locationTextView.setText("Location is null")
            return
        }
        latitude = location.latitude
        longitude = location.longitude
        val latitudeStr = Location.convert(latitude, Location.FORMAT_SECONDS)
        val longitudeStr = Location.convert(longitude, Location.FORMAT_SECONDS)
        locationTextView.setText("N:$latitudeStr, E:$longitudeStr, Locs:${numOfLocations}, Cnt:${locationGetsCounter}")
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