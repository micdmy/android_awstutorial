package com.example.awstutorial

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment() {
    //private lateinit var map : MapView;
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var compassOverlay: CompassOverlay
    private lateinit var activityContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "onAttach")
        activityContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView")
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(activityContext, PreferenceManager.getDefaultSharedPreferences(activityContext));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, if you abuse osm's
        //tile servers will get you banned based on this string.
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")

        map.setTileSource(TileSourceFactory.OpenTopo);

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activityContext), map);
        locationOverlay.enableMyLocation();
        map.overlays.add(locationOverlay)

        compassOverlay = CompassOverlay(activityContext, InternalCompassOrientationProvider(activityContext), map);
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
        }, activityContext)
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
                Toast.makeText(activityContext,"${p?.getLatitude()}  - ${p?.getLongitude()}",
                    Toast.LENGTH_LONG).show()
                Log.i(TAG, "onSingleTap")
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                Toast.makeText(activityContext,"${p?.getLatitude()}  - ${p?.getLongitude()}",
                    Toast.LENGTH_LONG).show()
                Log.i(TAG, "onLongPress")
                return false
            }

        }
        val overlayEvents = MapEventsOverlay(MReceive())
        map.getOverlays().add(overlayEvents)
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        map.onResume()
        //Configuration.getInstance().load(activityContext, PreferenceManager.getDefaultSharedPreferences(activityContext));
        //map.setTileSource(TileSourceFactory.OpenTopo);

        //map.overlays.add(locationOverlay)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
        map.onPause()
    }

    companion object {
        private const val TAG = "MapFragment"
    }
}