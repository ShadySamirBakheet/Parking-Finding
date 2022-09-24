package com.apps.parkingfinding.views.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.parkingfinding.R
import com.apps.parkingfinding.adapters.PlaceAdapter
import com.apps.parkingfinding.adapters.TransportationAdapter
import com.apps.parkingfinding.data.models.BookedStatus
import com.apps.parkingfinding.data.models.Parking
import com.apps.parkingfinding.data.models.ParkingBooked
import com.apps.parkingfinding.databinding.ActivityHomeBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.utils.Constants.isAdmin
import com.apps.parkingfinding.utils.Methods
import com.apps.parkingfinding.viewmodel.NetworkViewModel
import com.apps.parkingfinding.viewmodel.NotificationViewModel
import com.apps.parkingfinding.viewmodel.ParkingViewModel
import com.apps.parkingfinding.views.notification.NotificationActivity
import com.apps.parkingfinding.views.parking.AddParkingActivity
import com.apps.parkingfinding.views.parking.ParkingDetailsActivity
import com.apps.parkingfinding.views.qr.ScanQrActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var placeAdapter: PlaceAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var parkingViewModel: ParkingViewModel
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var notificationViewModel: NotificationViewModel

    var isSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        parkingViewModel = ViewModelProvider(this)[ParkingViewModel::class.java]
        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CAMERA,
                ), 1
            )

        }

        if (Constants.isAdmin) {
            binding.icon.visibility = View.VISIBLE
            binding.titleHome.visibility = View.GONE
            binding.transportationList.visibility = View.GONE
            binding.transportation.visibility = View.GONE
            binding.scan.visibility = View.GONE
            binding.title.text = getString(R.string.welcome_admin)

            binding.addParking.setOnClickListener {
                startActivity(
                    Intent(this, AddParkingActivity::class.java)
                )
            }

            binding.place.text = "Your Garage"
        } else {
            val transportationAdapter = TransportationAdapter(this@HomeActivity)
            binding.transportationList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = transportationAdapter
            }

            transportationAdapter.setOnSaveListener {
                isSelected = true
            }

            binding.place.text = "Recent place"
            binding.icon.visibility = View.GONE
            binding.addParking.visibility = View.GONE
            binding.titleHome.visibility = View.VISIBLE
            binding.title.text = getString(R.string.your_location)
        }
        placeAdapter = PlaceAdapter(this@HomeActivity, supportFragmentManager)
        placeAdapter.setOnClickListener { id, uid ->
            if (isSelected || isAdmin) {
                startActivity(
                    Intent(this, ParkingDetailsActivity::class.java).putExtra("id", id)
                        .putExtra("uid", uid)
                )
            } else {
                Toast.makeText(this, "Please choose your transportatioin", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        placeAdapter.setOnDeleteListener {
            showDialogFun(it)
        }
        binding.placesList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = placeAdapter
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    binding.address.text =
                        Methods.getAddress(this, location.latitude, location.longitude)!!
                    getParking(location.latitude, location.longitude)
                }

            }

        getParking(5.5, 5.5)

        binding.notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.scan.visibility = View.GONE



        binding.scan.setOnClickListener {
            startActivity(Intent(this, ScanQrActivity::class.java))
        }

        getNoty()
    }

    private fun getNoty() {
        var uid = ""
        if (Constants.isAdmin) uid = "Admin"
        if (!Constants.isAdmin) uid = FirebaseAuth.getInstance().uid ?: ""
        notificationViewModel.getNotificationData(uid).observe(this) {
            binding.count.text = (it?.children?.count() ?: 0).toString()
        }
    }

    private fun getParking(latitude: Double, longitude: Double) {
        networkViewModel.networkState(this).observe(this) {
            if (it) {
                parkingViewModel.getParkingData().observe(this) {

                    binding.progress.visibility = View.VISIBLE
                    val list = ArrayList<Parking>()

                    it?.children?.forEach { dataSnapshot ->
                        dataSnapshot.children.forEach { parking ->
                            val parkingItem = parking.getValue(Parking::class.java)
                            if (parkingItem != null) {
                                list.add(parkingItem)
                                parking?.child("booked")?.children?.forEach { book ->
                                    book.children.forEach { book2 ->
                                        val parkingBooked =
                                            book2.getValue(ParkingBooked::class.java)

                                        if (parkingBooked?.userId == FirebaseAuth.getInstance().currentUser?.uid && parkingBooked?.status != BookedStatus.finishParking) {
                                            Constants.parkingBooked = parkingBooked
                                            Constants.selectParking = parkingItem
                                            binding.scan.visibility = View.VISIBLE
                                        } else {
                                            binding.scan.visibility = View.GONE
                                        }

                                    }
                                }
                            }

                        }

                    }
                    binding.progress.visibility = View.GONE
                    if (list.isNotEmpty()){
                        placeAdapter.addData(list, latitude, longitude)
                        binding.placesList.visibility=View.VISIBLE
                    }
                    else{
                        binding.placesList.visibility=View.GONE
                    }
                }
            }
        }
    }

    private fun showDialogFun(parking: Parking) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Parking").setMessage("Are you want to Delete ${parking.name}")
            .setPositiveButton("Ok") { dialogInterface, _ ->
                binding.progress.visibility = View.VISIBLE
                parkingViewModel.deleteParking(parking).addOnCanceledListener {
                    Toast.makeText(this, "Remove Done", Toast.LENGTH_SHORT).show()
                    binding.progress.visibility = View.GONE
                    dialogInterface.dismiss()
                }
            }.setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        builder.create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    binding.address.text =
                        Methods.getAddress(this, location.latitude, location.longitude)!!
                    getParking(location.latitude, location.longitude)
                }

            }
    }
}