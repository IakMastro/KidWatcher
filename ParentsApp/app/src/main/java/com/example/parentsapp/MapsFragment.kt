package com.example.parentsapp

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        val dbHandler = DBHandler("GPS")

        val thread = Runnable {
            val gps = dbHandler.getGPS()
            val position = LatLng(gps.latitude.toDouble(), gps.longitude.toDouble())
            googleMap.addMarker(MarkerOptions().position(position).title("Your child's position"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(position))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f))
        }

        val handler = Handler()
        handler.postDelayed(thread, 1000)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}