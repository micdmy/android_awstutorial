package com.example.awstutorial

import android.content.*
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var locationGetsCounter: UInt = 0u;
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var locationServiceManager: LocationServiceManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        // prepare our List view and RecyclerView (cells)
        setupRecyclerViewItems(item_list)

        setupAuthButton(UserData)

        UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
            // update UI
            Log.i(TAG, "isSignedIn changed : $isSignedUp")

            //animation inspired by https://www.11zon.com/zon/android/multiple-floating-action-button-android.php
            if (isSignedUp) {
                fabAuth.setImageResource(R.drawable.ic_baseline_lock_open)
                Log.d(TAG, "Showing fabADD")
                fabItemAdd.show()
            } else {
                fabAuth.setImageResource(R.drawable.ic_baseline_lock)
                Log.d(TAG, "Hiding fabADD")
                fabItemAdd.hide()
            }
        })

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

        fabOpenMap.setOnClickListener {
            startActivity(Intent(this, OsmActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
        locationServiceManager = LocationServiceManager(this)
        locationServiceManager.bind()
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
        locationServiceManager.unbind()
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
    private fun setupRecyclerViewItems(recyclerView: RecyclerView) {
        // update individual cell when the Item data are modified
        UserData.items().observe(this, Observer<MutableList<UserData.Item>> { items ->
            Log.d(TAG, "Item observer received ${items.size} items")

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

                // remove to item from the userdata will refresh the UI
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