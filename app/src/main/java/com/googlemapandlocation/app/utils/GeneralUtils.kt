package com.googlemapandlocation.app.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory




object GeneralUtils {
    fun GoogleMap.addMarker(Latitude: Double, Longitude: Double,title:String) {
        this.addMarker(MarkerOptions().position(LatLng(Latitude, Longitude)).title(title))
        this.moveCamera(CameraUpdateFactory.newLatLng(LatLng(Latitude, Longitude)))

    }
}