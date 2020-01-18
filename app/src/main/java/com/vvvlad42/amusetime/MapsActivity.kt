package com.vvvlad42.amusetime

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.VisibleRegion
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.vvvlad42.amusetime.data.CoordinatesService
import com.vvvlad42.amusetime.data.LocationParcel
import com.vvvlad42.amusetime.data.PlaceLocation
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback , OnMarkerClickListener,
    GoogleMap.OnCameraIdleListener ,PlaceSelectionListener{


    private val TAG = "MapsActivity"
    private lateinit var gm: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var coService : CoordinatesService? = null
    private var allLocations = mutableListOf<PlaceLocation>()
    private lateinit var clusterManager:ClusterManager<MapMarker>
    private lateinit var mAdView : AdView

    override fun onMarkerClick(marker: Marker?):Boolean{
        Toast.makeText(
            this,
            "מחפש כתובת...",
            Toast.LENGTH_SHORT
        ).show()
        if (marker?.position!= null){
            val titleStr = getAddress(marker.position)  // add these two lines
            marker.title = titleStr
        }

        return false
    }
    override fun onCameraIdle() {
        val visibleRegion: VisibleRegion = gm.projection.visibleRegion
        val sW = visibleRegion.latLngBounds.southwest
        val nE = visibleRegion.latLngBounds.northeast
        val locGetter =  AsyncLocationsGetter(this)
        //This will invoke doInBackground
        locGetter.execute(sW, nE)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


//        setSupportActionBar(toolbar)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        //keep this line only to remind me where to look for the db files
        var absPath = this.filesDir.absolutePath

        coService = CoordinatesService(this.applicationContext)

        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

//                Toast.makeText(applicationContext, "Updated user location", Toast.LENGTH_SHORT).show()
                lastLocation = p0.lastLocation
//                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
//                placeMarkerOnMap(currentLatLng, R.drawable.ic_your_loc_1)

            }
        }
        createLocationRequest()
        createAds()


//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//
//            /**
//             * Initialize Places. For simplicity, the API key is hard-coded. In a production
//             * environment we recommend using a secure mechanism to manage API keys.
//             */
//            if (!Places.isInitialized()) {
//                Places.initialize(applicationContext, "key")
//            }
//            val fields = listOf(Place.Field.ID, Place.Field.NAME)
//            var autocompleteIntent =
//                Autocomplete.IntentBuilder(
//                    AutocompleteActivityMode.OVERLAY,
//                    fields
//                ).build(this)
//            startActivityForResult(autocompleteIntent, 5)
//
//        }
    }

    private fun createAds() {
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (mAdView.visibility == View.GONE) {
                    mAdView.visibility = View.VISIBLE
                }
            }

            override fun onAdFailedToLoad(errorCode : Int) {
                // Code to be executed when an ad request fails.
                if (mAdView.visibility == View.VISIBLE) {
                    mAdView.visibility = View.GONE
                }
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }

    private fun configureToolbar(){
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true) // switch on the left hand icon
            actionBar.setHomeAsUpIndicator(R.drawable.ic_play_loc_2) // replace with your custom icon
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        configureToolbar()
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.action_about){
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            return true
        }
        if(item.itemId==R.id.action_share){
//            val currentLatLng:LatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            val locationParcel = LocationParcel(lastLocation.latitude, lastLocation.longitude)
            val shareLocationActivity = Intent(this, ShareLocation::class.java)
            shareLocationActivity.putExtra("SharedLocation", locationParcel)
            startActivity(shareLocationActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 5) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Toast.makeText(applicationContext,"Found place "+ place.name,Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        gm=googleMap
        gm.uiSettings.isZoomControlsEnabled = true

        //map.mapType = GoogleMap.MAP_TYPE_TERRAIN

        //Enables the my-location layer which draws a light blue dot on the user's location.
        //Also adds button to center to location
        gm.isMyLocationEnabled = true
        //make sure have the permissions for location.
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
//                placeMarkerOnMap(currentLatLng, R.drawable.ic_your_loc_1)
                this.gm.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
        }
        // Initialize the AutocompleteSupportFragment.
//        val autocompleteFragment:PlaceAutocompleteFragment? =
//            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment)
//                    as PlaceAutocompleteFragment?

//        autocompleteFragment?.setOnPlaceSelectedListener(){
//
//        }
//        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//                onPlaceSelectedOverride(place)
//                Log.d("myTag", place.address + " - " + place.addressComponents + " - " + place.latLng)
//            }
//
//            override fun onError(status: Status) {
//                throw RuntimeException()
//            }
//        })
        clusterManager = ClusterManager(this, gm)

        gm.setOnMarkerClickListener(this)
        gm.setOnCameraIdleListener(this)


//        val visibleRegion: VisibleRegion = gm.projection.visibleRegion
//
//        val sW = visibleRegion.latLngBounds.southwest
//        val nE = visibleRegion.latLngBounds.northeast
//        val locGetter =  AsyncLocationsGetter(this)
//        //This will invoke doInBackground
//        locGetter.execute(sW, nE)

    }





    private fun placeMarkerOnMap(location: LatLng, bitmapId: Int) {
//        val titleStr = getAddress(location)  // add these two lines

//        val markerOptions = MarkerOptions().position(location)
        //default marker but different color
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//        markerOptions.title(titleStr)

        //custom marker, need to import bitmap
//        markerOptions.icon(
//            BitmapDescriptorFactory.fromBitmap(
//            BitmapFactory.decodeResource(resources, bitmapId)))
//        gm.addMarker(markerOptions)

        clusterManager.addItem(MapMarker("", location))

    }
    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale("iw", "IL"))
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && addresses.isNotEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex + 1) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }
    private fun drawPlaces(places: List<PlaceLocation>?){

        places?.forEach {
            //Kind of cache to prevent redrawing of already existing location
            if (it !in allLocations) {
                allLocations.add(it)

                val mark = LatLng(it.lat, it.lng)
                placeMarkerOnMap(mark, R.drawable.ic_play_loc_2)
            }
//            val co:List<Any>? = it.geometry?.coordinates
//            if(it.geometry?.type =="Polygon"){
//                val inlst:List<List<Double>>? = co?.get(0) as List<List<Double>>?
//                    //(co?.get(0) as List<Any>?)?.get(0) as List<Any>?
//                val polOpt = PolygonOptions()
//                inlst?.forEach {
//                    polOpt.add(LatLng(it[1] as? Double?: 0.0, it[0] as? Double?: 0.0))
//                }
//                if(isInView(polOpt.points[0], nE,sW)){
//                    map.addPolygon(polOpt.fillColor(Color.argb(50,150,50, 50)).strokeColor(Color.GREEN))
//                    placeMarkerOnMap(polOpt.points[0], R.drawable.ic_play_loc_2)
//                }
//
//            } else{
//
//                val lat:Double = co?.get(0) as? Double?: 0.0
//                val lng:Double = co?.get(1) as? Double?: 0.0
//                val mark = LatLng(lng,lat)
//                if (isInView(mark, nE, sW))
//                    placeMarkerOnMap(mark, R.drawable.ic_play_loc_2)
//            }
        }

    }

//    This is an implementation of threading to get data, needs to extend AsyncTask
    private class AsyncLocationsGetter internal constructor(context: MapsActivity):  AsyncTask<LatLng, Int,List<PlaceLocation>?>(){
        private val activityReference: WeakReference<MapsActivity> = WeakReference(context)
        /**
         * This is the method that is called when doInBackground completes
         * This method runs in GUI thread, so can make changes to GUI
         */
        override fun onPostExecute(result: List<PlaceLocation>?) {
            super.onPostExecute(result)
            val context = activityReference.get()
            if (context == null || context.isFinishing) return
            context.drawPlaces(result)
            context.clusterManager.cluster()
        }
        /**
         * Implementing this function for future use when will be pulling data over the network
         * the searchString param may be used to filter the query, for now not in use
         * This method runs in a separate thread
         * vararg means that can get one or many parameters
         * can use it for example like this to get the first value:
         * var firstFilter = filterParam[0]]
         */
        override fun doInBackground(vararg filterArg: LatLng): List<PlaceLocation>? {
            val context = activityReference.get()
            if (context == null || context.isFinishing) return emptyList()
            return context.coService?.readLocations(filterArg[0], filterArg[1])
        }
    }
    private class MapMarker(val mtitle : String, val latLng:LatLng): ClusterItem{
        override fun getSnippet(): String { return "" }
        override fun getTitle(): String {return mtitle}
        override fun getPosition(): LatLng {return latLng}
    }

    //companion object is like a static function that is tied to a class, not an instance
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }
    private fun createLocationRequest() {

        locationRequest = LocationRequest()

        locationRequest.interval = 10000

        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)


        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())


        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MapsActivity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    //Implementation of functions for autocomplete place selection
    override fun onPlaceSelected(place: Place) {
        Log.i(TAG, "Place: " + place.name + ", " + place.id)
        Toast.makeText(applicationContext,""+ place.name+ place.latLng, Toast.LENGTH_LONG).show()
    }

    override fun onError(status: Status) {
        Log.i(TAG, "An error occurred: $status")
        Toast.makeText(applicationContext,""+status.toString(),Toast.LENGTH_LONG).show()
    }

}
