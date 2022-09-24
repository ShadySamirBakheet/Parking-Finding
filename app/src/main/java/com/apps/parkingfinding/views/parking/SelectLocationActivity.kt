package com.apps.parkingfinding.views.parking

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.apps.parkingfinding.R
import com.apps.parkingfinding.databinding.ActivitySelectLocationBinding
import com.apps.parkingfinding.utils.Methods
import com.apps.parkingfinding.viewmodel.NetworkViewModel
import com.apps.parkingfinding.viewmodel.ParkingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySelectLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var parkingViewModel: ParkingViewModel
    private lateinit var networkViewModel: NetworkViewModel

    private var marker: Marker? = null

    var isView = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        parkingViewModel = ViewModelProvider(this)[ParkingViewModel::class.java]

        isView = intent.getBooleanExtra("isView", false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.back.setOnClickListener {
            finish()
        }
        binding.confirm.setOnClickListener {
            if (marker != null) {

                val latLng = marker?.position
                if (latLng != null) {
                    parkingViewModel.selLocation(
                        latLng,
                        Methods.getAddress(this, latLng.latitude, latLng.longitude)!!
                    )
                }
                finish()
            } else {
                Toast.makeText(this, "Please Select Location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

        if (isView) {
            binding.confirm.visibility = View.GONE
            ParkingViewModel.locationName?.observe(
                this
            ) {
                binding.nameLocation.text = it
            }
            ParkingViewModel.location!!.observe(
                this
            ) { latLng ->

                val name = Methods.getAddress(this, latLng!!.latitude, latLng.longitude)
                mMap.addMarker(MarkerOptions().position(latLng).title(name))
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15.5f))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }

        } else {
            binding.confirm.visibility = View.VISIBLE

            mMap.setOnMapClickListener {
                val name = Methods.getAddress(this, it.latitude, it.longitude)
                if (marker != null) {
                    binding.nameLocation.text = name
                    marker?.position = it
                    marker?.title = name
                } else {
                    marker = mMap.addMarker(MarkerOptions().position(it).title(name))

                }

            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                    val name = Methods.getAddress(this, latLng.latitude, latLng.longitude)
                    binding.nameLocation.text = name
                    marker = mMap.addMarker(MarkerOptions().position(latLng).title(name))
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.5f))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

                }
        }


    }

}