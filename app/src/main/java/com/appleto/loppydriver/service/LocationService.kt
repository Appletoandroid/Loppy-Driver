package com.appleto.loppydriver.service

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import android.os.Bundle
import android.content.Intent
import android.os.IBinder
import android.app.Service
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.PrefUtils
import com.appleto.loppydriver.helper.Utils
import com.appleto.loppydriver.retrofit2.ApiService
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LocationService : Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var currentlyProcessingLocation = false
    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null

    override fun onCreate() {
        super.onCreate()
//        Toast.makeText(this, "Created", Toast.LENGTH_LONG).show()
//        if (!currentlyProcessingLocation) {
//            currentlyProcessingLocation = true
//            Toast.makeText(this, "Service not Start Command", Toast.LENGTH_LONG).show()
        startTracking()
//        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
//        Toast.makeText(this, "Service Start Command", Toast.LENGTH_LONG).show()
//        if (!currentlyProcessingLocation) {
//            currentlyProcessingLocation = true
//            Toast.makeText(this, "Service not Start Command", Toast.LENGTH_LONG).show()
        startTracking()
//        }

        return START_NOT_STICKY
    }

    private fun startTracking() {
        Log.d(TAG, "startTracking")


        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        if (!googleApiClient!!.isConnected || !googleApiClient!!.isConnecting) {
            googleApiClient!!.connect()
        }
        //} else {
        //    Log.e(TAG, "unable to connect to google play services.");
        //}
    }

    protected fun sendLocationDataToWebsite(location: Location) {
        val apiService by lazy {
            PrefUtils.getStringValue(application, Const.TOKEN)?.let { ApiService.create(it) }
        }

        val disposable =
            PrefUtils.getStringValue(application, Const.USER_ID)?.let {
                apiService
                    ?.updateDriverLocation(it, location.latitude, location.longitude)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(
                        { result ->
                            if (result.has("status") && result.get("status").asInt == 1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Utils.scheduleJob(this)
                                }
                            } else {
                                Toast.makeText(
                                    application,
                                    result.get("message").asString,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        { error ->
                            Toast.makeText(application, error.localizedMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    )
            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            Log.e(
                TAG,
                "position: " + location!!.getLatitude() + ", " + location!!.getLongitude() + " accuracy: " + location!!.getAccuracy()
            )

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (location!!.getAccuracy() < 500.0f) {
                stopLocationUpdates()
                sendLocationDataToWebsite(location!!)
            }
        }
    }

    private fun stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            googleApiClient!!.disconnect()
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "onConnected")

        locationRequest = LocationRequest.create()
        locationRequest!!.interval = 10000 // milliseconds
        locationRequest!!.fastestInterval =
            5000 // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
            )
        } catch (se: SecurityException) {
            Log.e(TAG, "Go into settings and find Gps Tracker app and enable Location.")
        }

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed")

        stopLocationUpdates()
        stopSelf()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.e(TAG, "GoogleApiClient connection has been suspended.")
    }

    companion object {

        private val TAG = "LocationService"
        private val PERMISSION_ACCESS_FINE_LOCATION = 1
    }
}