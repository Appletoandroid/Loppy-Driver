package com.appleto.loppydriver.activity

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppydriver.R
import com.appleto.loppydriver.apiModels.PendingRideDatum
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.PrefUtils
import com.appleto.loppydriver.viewModel.MainActivityViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var mDriverMarker: Marker? = null
    var mEndPointMarker: Marker? = null
    private var data: PendingRideDatum? = null
    private var viewModel: MainActivityViewModel? = null
    private var reachedSource = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        data = intent?.extras?.getSerializable(Const.JOB_DATA) as PendingRideDatum

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    createLocationRequest()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }

            }).check()
    }

    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder?.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MapsActivity,
                        101
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            createLocationRequest()
        }
    }

    private fun setDriverMarker(latitude: Double, longitude: Double) {
        if (mDriverMarker != null) {
            mDriverMarker?.remove()
        }

        val markerOptions = MarkerOptions()
        markerOptions.draggable(true)
        markerOptions.position(LatLng(latitude, longitude))
        markerOptions.title("Current Location")
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN
            )
        )
        mDriverMarker = mMap.addMarker(markerOptions)

    }

    private fun setEndPointMarker(latitude: Double, longitude: Double) {
        if (mEndPointMarker != null) {
            mEndPointMarker?.remove()
        }

        val markerOptions = MarkerOptions()
        markerOptions.draggable(true)
        markerOptions.position(LatLng(latitude, longitude))
        markerOptions.title("Destination Location")
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ORANGE
            )
        )
        mEndPointMarker = mMap.addMarker(markerOptions)
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
        mMap = googleMap

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...
                    if (location != null) {
                        setDriverMarker(location.latitude, location.longitude)
                        if (reachedSource == "1") {
                            getDirectionRoute(data?.destinationLat, data?.destinationLong)
                        } else {
                            getDirectionRoute(data?.sourceLat, data?.sourceLong)
                        }

                        PrefUtils.getStringValue(this@MapsActivity, Const.USER_ID)?.let {
                            viewModel?.updateDriverLocation(
                                this@MapsActivity,
                                it, location.latitude, location.longitude
                            )
                        }
//                        fusedLocationClient.removeLocationUpdates(locationCallback)
                    }
                }
            }
        }
    }

    private fun getDirectionRoute(latitude: String?, longitude: String?) {
        val path = ArrayList<LatLng>()

        if (mDriverMarker != null) {
            val context = GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build()

            val req = DirectionsApi.getDirections(
                context,
                "${mDriverMarker?.position?.latitude},${mDriverMarker?.position?.longitude}",
                "$latitude,$longitude"
            )

            try {
                val res = req.await()

                if (res.routes != null && res.routes.isNotEmpty()) {
                    val route = res.routes[0]

                    if (route.legs != null) {
                        val legThis = route.legs[0]
                        var distanceKm = 0.0
                        if (legThis.distance.humanReadable.contains("km")) {
                            distanceKm =
                                legThis.distance.humanReadable.replace(" km", "").toDouble()

                        } else {
                            distanceKm =
                                legThis.distance.humanReadable.replace(" m", "").toDouble() / 1000
                        }
                        if (distanceKm < 1) {
                            reachedSource = "1"
                        }
                        for (leg in route.legs) {
                            if (leg.steps != null) {
                                for (step in leg.steps) {
                                    if (step.steps != null && step.steps.isNotEmpty()) {
                                        for (step1 in step.steps) {
                                            val point1 = step1.polyline
                                            if (point1 != null) {
                                                val coords1 = point1.decodePath()
                                                for (coord1 in coords1) {
                                                    path.add(LatLng(coord1.lat, coord1.lng))
                                                }
                                            }
                                        }
                                    } else {
                                        val points = step.polyline
                                        if (points != null) {
                                            val coords = points.decodePath()
                                            for (coord in coords) {
                                                path.add(LatLng(coord.lat, coord.lng))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Draw poly line
            if (path.size > 0) {
                val opts = PolylineOptions().addAll(path).color(Color.BLACK).width(16F)
                mMap.addPolyline(opts)
            }

            mMap.uiSettings.isZoomControlsEnabled = true

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDriverMarker?.position?.latitude?.let {
                mDriverMarker?.position?.longitude?.let { it1 ->
                    LatLng(
                        it, it1
                    )
                }
            }, 17F))

            latitude?.toDouble()?.let {
                longitude?.toDouble()?.let { it1 ->
                    setEndPointMarker(
                        it,
                        it1
                    )
                }
            }
        }
    }

    override fun onStop() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        createLocationRequest()
    }
}
