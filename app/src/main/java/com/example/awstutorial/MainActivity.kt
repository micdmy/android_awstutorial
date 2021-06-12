package com.example.awstutorial

import android.content.*
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

        setupTabs()


        fabGetLocation.setOnClickListener {
            locationServiceManager.requestLocationUpdates(findViewById(android.R.id.content))
        }

        locationTextView.setOnClickListener {
            displayLocation(locationServiceManager.getLastLocation(), 123)
        }

    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
        locationServiceManager = LocationServiceManager(this)
        locationServiceManager.bind {}
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

    private fun setupTabs() {
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab){
                when(tab.position) {
                    0 -> {
                        supportFragmentManager.commit {
                            replace<PlayerHomeFragment>(R.id.frame_layout)
                            setReorderingAllowed(true)
                        }
                    }
                    1 -> {
                        supportFragmentManager.commit {
                            replace<MapFragment>(R.id.frame_layout)
                            setReorderingAllowed(true)
                        }
                    }
                    2 -> {
                        supportFragmentManager.commit {
                            replace<QuestsFragment>(R.id.frame_layout)
                            setReorderingAllowed(true)
                        }
                    }
                    else -> {
                        supportFragmentManager.commit {
                            replace<ActionsFragment>(R.id.frame_layout)
                            setReorderingAllowed(true)
                        }
                    }

                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        supportFragmentManager.commit {
            replace<PlayerHomeFragment>(R.id.frame_layout)
            setReorderingAllowed(true)
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