package com.googlemapandlocation.app.ui.activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.googlemapandlocation.app.R
import com.googlemapandlocation.app.utils.GeneralUtils.addMarker
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import com.google.android.gms.tasks.OnCompleteListener
import java.lang.Exception
import android.location.LocationManager





class MapsActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var mMap: GoogleMap
    lateinit var supportMapFragment:SupportMapFragment
    var gpsSettingEnabled:Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
         supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission()
    }






    //getLocationPermission
    @SuppressLint("MissingPermission")
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if(gpsSettingEnabled) {

                deviceCurrentLocation()
            }
            else{
                enableGpsSetting()
            }

        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        }

    }



  //Function of Showing Current Location
    fun deviceCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }
        val task: Task<Location> =  fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener(OnSuccessListener { location ->
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (location != null) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    supportMapFragment.getMapAsync(OnMapReadyCallback {
                        mMap = it
                        currentLocation = location
                        Toast.makeText(
                            applicationContext,
                            currentLocation.latitude.toString() + "" + currentLocation.longitude.toString(),
                            Toast.LENGTH_SHORT

                        ).show()
                        mMap.addMarker(
                            currentLocation.latitude,
                            currentLocation.longitude,
                            "we are here"
                        )
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLng(
                                LatLng(
                                    currentLocation.latitude,
                                    currentLocation.longitude
                                )
                            )
                        )
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    currentLocation.latitude,
                                    currentLocation.longitude
                                ), 50f
                            )
                        )
                    })

                }
            }
        })
    }

    //Function  onRequestPermissionsResult
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            200 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationPermission()
                    Toast.makeText(this, "allowed", Toast.LENGTH_SHORT).show()

                }
                else{
                    Toast.makeText(this, "you never allowed", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    //enable GPS setting
    fun enableGpsSetting() {
        val location: LocationRequest = LocationRequest.create()
        location.interval = 50L
        location.fastestInterval = 1000L
        location.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(location)
            .setAlwaysShow(true)
        LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
            .addOnCompleteListener()
                {
                    gpsSettingEnabled=true

                    getLocationPermission()

                }
            .addOnFailureListener(this,object:OnFailureListener {




                override fun onFailure(p0: Exception) {
                    if (p0 is ResolvableApiException) {
                        try {

                            // and check the result in onActivityResult().
                            val resolvable = p0 as ResolvableApiException
                            resolvable.startResolutionForResult(this@MapsActivity,
                                201);
                        } catch (sendExcep:IntentSender.SendIntentException) {

                            Toast.makeText(this@MapsActivity," gps Not enabled" +sendExcep.message, Toast.LENGTH_SHORT).show()

                        }

                    }
                }
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    if(requestCode==201){
        if(Activity.RESULT_OK==resultCode){
            gpsSettingEnabled=true
            getLocationPermission()
        }
        else{
            Toast.makeText(this, "not enabled", Toast.LENGTH_SHORT).show()

        }
    }
    }



}