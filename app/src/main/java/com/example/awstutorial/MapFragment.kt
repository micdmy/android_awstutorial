package com.example.awstutorial

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private lateinit var sharedPreferences: SharedPreferences

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
        Configuration.getInstance().load(activityContext, PreferenceManager.getDefaultSharedPreferences(activityContext)); //TODO w tutorialu gdzie indziej
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


        sharedPreferences = activityContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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
        map.setTileSource(TileSourceFactory.OpenTopo);

        val zoomLevel = sharedPreferences.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1F).toDouble()
        val orientation = sharedPreferences.getFloat(PREFS_ORIENTATION, 0F)
        val latitude = sharedPreferences.getString(PREFS_LATITUDE_STRING, "1.0")?.toDouble() ?: 1.0
        val longitude = sharedPreferences.getString(PREFS_LONGITUDE_STRING, "1.0")?.toDouble() ?: 1.0
        map.controller.setZoom(zoomLevel)
        map.setMapOrientation(orientation, true)
        map.setExpectedCenter(GeoPoint(latitude, longitude))

        map.onResume()
        //Configuration.getInstance().load(activityContext, PreferenceManager.getDefaultSharedPreferences(activityContext));
        //map.setTileSource(TileSourceFactory.OpenTopo);

        //map.overlays.add(locationOverlay)
    }

    override fun onPause() {
        Log.i(TAG, "onPause")

        val edit = sharedPreferences.edit()
        edit.putString(PREFS_TILE_SOURCE, map.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, map.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, map.getMapCenter().getLatitude().toString());
        edit.putString(PREFS_LONGITUDE_STRING, map.getMapCenter().getLongitude().toString());
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, map.getZoomLevelDouble().toFloat());
        edit.commit()
        map.onPause()
        super.onPause()
    }

    companion object {
        private const val TAG = "MapFragment"
        private const val PREFS_NAME = "org.andnav.osm.prefs"
        private const val PREFS_TILE_SOURCE = "tilesource"
        private const val PREFS_LATITUDE_STRING = "latitudeString"
        private const val PREFS_LONGITUDE_STRING = "longitudeString"
        private const val PREFS_ORIENTATION = "orientation"
        private const val PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble"

    }
}