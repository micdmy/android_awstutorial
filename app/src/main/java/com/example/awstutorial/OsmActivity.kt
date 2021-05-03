package com.example.awstutorial

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_osm.*
//import com.niels_ole.customtileserver.R

import org.osmdroid.config.Configuration.*
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class OsmActivity : AppCompatActivity() {
    private lateinit var map : MapView;
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var compassOverlay: CompassOverlay
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, if you abuse osm's
        //tile servers will get you banned based on this string.

        //inflate and create the map
        setContentView(R.layout.activity_osm);

        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.OpenTopo);

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map);
        locationOverlay.enableMyLocation();
        map.overlays.add(locationOverlay)

        compassOverlay = CompassOverlay(this, InternalCompassOrientationProvider(this), map);
        compassOverlay.enableCompass();
        map.overlays.add(compassOverlay);

        val rotationGestureOverlay = RotationGestureOverlay(map);
        rotationGestureOverlay.isEnabled
        map.setMultiTouchControls(true);
        map.overlays.add(rotationGestureOverlay);

        //your items
        val items = ArrayList<OverlayItem>()
        items.add(OverlayItem("Title", "Description", GeoPoint(0.0, 0.0)))
        var overlay = ItemizedOverlayWithFocus<OverlayItem>(items, object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                //do something
                Log.i(TAG, "onItemSingleTapUp")
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                Log.i(TAG, "onItemLongPress")
                return false
            }
        }, this)
        overlay.setFocusItemsOnTap(true);
        map.overlays.add(overlay);

        //Marker V2 google api:
        val startMarker = Marker(map)
        startMarker.setPosition(GeoPoint(51.0, 17.0))
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_stars));
        startMarker.setTitle("My marker");
        map.overlays.add(startMarker)

        class MReceive : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                Toast.makeText(getBaseContext(),"${p?.getLatitude()}  - ${p?.getLongitude()}",Toast.LENGTH_LONG).show()
                Log.i(TAG, "onSingleTap")
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                Toast.makeText(getBaseContext(),"${p?.getLatitude()}  - ${p?.getLongitude()}",Toast.LENGTH_LONG).show()
                Log.i(TAG, "onLongPress")
                return false
            }

        }
        val overlayEvents = MapEventsOverlay(MReceive())
        map.getOverlays().add(overlayEvents)

        cancelButton.setOnClickListener {
            this.finish()
        }
    }
    override fun onResume() {
        super.onResume();
        Log.i(TAG, "onResume")
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause();
        Log.i(TAG, "onPause")
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
    companion object {
        private const val TAG = "OsmActivity"
    }
}