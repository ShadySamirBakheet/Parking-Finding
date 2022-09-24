package com.apps.parkingfinding.views.parking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.parkingfinding.adapters.AddImageAdapter
import com.apps.parkingfinding.adapters.LocationAdapter
import com.apps.parkingfinding.data.models.Parking
import com.apps.parkingfinding.databinding.ActivityAddParkingBinding
import com.apps.parkingfinding.utils.FileUtils
import com.apps.parkingfinding.viewmodel.NetworkViewModel
import com.apps.parkingfinding.viewmodel.ParkingViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class AddParkingActivity : AppCompatActivity() {
    private var sendingFile: File? = null

    private lateinit var binding: ActivityAddParkingBinding
    private lateinit var parkingViewModel: ParkingViewModel
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var addImageAdapter: AddImageAdapter

    private var location: LatLng? = null
    private var locationName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddParkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        parkingViewModel = ViewModelProvider(this)[ParkingViewModel::class.java]
        addImageAdapter = AddImageAdapter(this)

        addImageAdapter.setOnAddListener {
            loadImage()
        }

        binding.confirm.setOnClickListener {
            networkViewModel.networkState(this).observe(this) {
                if (it) {
                    Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()
                    saveParking()
                }
            }
        }

        binding.addImageList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = addImageAdapter
        }



        binding.addLocation.setOnClickListener {
            startActivity(Intent(this, SelectLocationActivity::class.java))
        }
    }

    private fun saveParking() {
        try {
            val name = binding.parkName.text.toString().trim()
            val slots = binding.slots.text.toString().trim().toInt()
            val price = binding.price.text.toString().trim().toDouble()

            val images = addImageAdapter.data

            if (name.isNotEmpty() && slots > 0 && price > 0 && location != null && images.isNotEmpty()) {
                binding.progress.visibility = View.VISIBLE
                val parking = Parking(
                    FirebaseAuth.getInstance().currentUser?.uid,
                    System.currentTimeMillis().toString(),
                    name,
                    slots,
                    0,
                    price,
                    location?.latitude!!,
                    location?.longitude!!,
                    locationName,
                )
                parkingViewModel.setParkingDate(parking).addOnCompleteListener {
                    if (it.isSuccessful) {
                        parkingViewModel.saveParkingImage(images, parking.id!!, parking.uid!!, this)
                        finish()
                    }

                    binding.progress.visibility = View.GONE
                }
            } else {
                Toast.makeText(
                    this,
                    "Data Error ${name.isNotEmpty()} slots ${slots > 0}&& price ${price > 0} && lo ${location != null} && images ${images.isNotEmpty()}}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //    binding.progress.visibility = View.GONE
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        if (ParkingViewModel.locationName != null) {
            ParkingViewModel.location?.observe(this) {
                location = it
            }
            ParkingViewModel.locationName?.observe(this) {

                if (it != null) {
                    locationName = it
                    val locationAdapter = LocationAdapter(this@AddParkingActivity, it)
                    locationAdapter.setOnDeleteListener {
                        parkingViewModel.clearLocation()
                    }
                    binding.locationList.apply {

                        isNestedScrollingEnabled = false
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = locationAdapter
                    }
                }
            }
        }
        super.onResume()
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.apply {
            type = "image/*"
        }
        startActivityForResult(intent, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                1 -> {
                    loadImageFun(data)
                }


            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadImageFun(data: Intent) {
        if (data.data != null) {
            val uri = data.data!!

            sendingFile = File(FileUtils.getSmartFilePath(this, data.data!!) ?: "")

            addImageAdapter.setImageURI(uri)

            Toast.makeText(this, "Send File", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error File", Toast.LENGTH_LONG).show()
        }
    }
}