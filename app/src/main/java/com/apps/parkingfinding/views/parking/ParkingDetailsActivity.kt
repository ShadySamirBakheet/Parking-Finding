package com.apps.parkingfinding.views.parking

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.apps.parkingfinding.R
import com.apps.parkingfinding.adapters.ImageSliderAdapter
import com.apps.parkingfinding.adapters.SlotAdapter
import com.apps.parkingfinding.data.models.*
import com.apps.parkingfinding.databinding.ActivityParkingDetailsBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.viewmodel.NetworkViewModel
import com.apps.parkingfinding.viewmodel.NotificationViewModel
import com.apps.parkingfinding.viewmodel.ParkingViewModel
import com.apps.parkingfinding.views.qr.ScanQrActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import java.util.*

class ParkingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParkingDetailsBinding
    private lateinit var adapterSlider: ImageSliderAdapter
    private lateinit var slotAdapter: SlotAdapter

    private lateinit var parkingViewModel: ParkingViewModel
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    val handler = Handler()

    var id = ""
    var uid = ""



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityParkingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        parkingViewModel = ViewModelProvider(this)[ParkingViewModel::class.java]
        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        id = intent.getStringExtra("id") ?: ""
        uid = intent.getStringExtra("uid") ?: ""

        slotAdapter = SlotAdapter(this)
        binding.slotsList.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 3)
            adapter = slotAdapter
        }
        adapterSlider = ImageSliderAdapter(this)
        binding.imageSlider.apply {
            setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION)
            setSliderAnimationDuration(1000)
            autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            scrollTimeInSec = 4 //set scroll delay in seconds :
            startAutoCycle()
            setSliderAdapter(adapterSlider)
        }
        getData()
        binding.back.setOnClickListener {
            finish()
        }
        if (Constants.isAdmin) {
            binding.confirm.visibility = View.GONE
        } else {
            binding.confirm.visibility = View.VISIBLE
            binding.confirm.setOnClickListener {
                saveSlotsSelect()
            }
        }


    }

    private fun saveSlotsSelect() {
        var num = slotAdapter.reserveds.size
        val parkingBooked = ParkingBooked(
            System.currentTimeMillis().toString(),
            id, FirebaseAuth.getInstance().currentUser?.uid, Date(), num
        )
        if (num > 0) {
            binding.progress.visibility = View.VISIBLE

            parkingViewModel.bookParking(uid, parkingBooked).addOnCompleteListener {
                if (it.isSuccessful) {
                        notificationViewModel.setNotificationData(
                            Notification(
                                Constants.user?.name.toString(),
                                "I am leave Parking",
                                System.currentTimeMillis().toString(),
                                "Admin",
                                Date(),
                            ),
                        )
                    binding.progress.visibility = View.GONE
                    confirmDialog()
                }
            }
        } else {
            Toast.makeText(this, "choose Slots please", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this,R.style.MyDialogTheme)
        val alertDialog = alertDialogBuilder.create()
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(
            R.layout.popup_slots_choosed, null
        )

        alertDialog.setView(dialogLayout)

        var s = ""
        slotAdapter.reserveds.forEach {
            s += " $it ,"
        }

        var timer = 60

        handler.postDelayed(object : Runnable {
            override fun run() {
                timer--
                dialogLayout.findViewById<TextView>(R.id.msg).text = "You have ${timer} Sec to scan QR code at the Parking"
                if (timer>0){
                    handler.postDelayed(this, 1000)
                }else{
                    if (alertDialog.isShowing){
                        deleteParking()
                        alertDialog.dismiss()
                    }

                }
            }
        }, 1000)




        dialogLayout.findViewById<TextView>(R.id.slots).text = s.substring(0, s.length - 1)
        dialogLayout.findViewById<TextView>(R.id.scan).setOnClickListener {
            startActivity(Intent(this, ScanQrActivity::class.java).putExtra("isEnter",true))
            finish()
            alertDialog.dismiss()
        }


        dialogLayout.findViewById<TextView>(R.id.cancel).setOnClickListener {
           deleteParking()
            alertDialog.dismiss()
        }

        alertDialog.show()

    }

    private fun deleteParking() {
        if (Constants.selectParking!=null){
            parkingViewModel.deleteBookParking(Constants.selectParking?.uid?:"",Constants.parkingBooked)
            Constants.parkingBooked = null
            Constants.selectParking = null
            finish()

        }
    }

    private fun getData() {
        binding.progress.visibility = View.VISIBLE
        networkViewModel.networkState(this).observe(this) {
            if (it) {
                parkingViewModel.getSelectParking(id, uid).observe(this) {
                    val parking = it?.getValue(Parking::class.java)

                    binding.location.setOnClickListener {
                        parkingViewModel.selLocation(
                            LatLng(parking?.locationLat?:0.0,parking?.locationLng?:0.0),
                           parking?.locationName?:""
                        )
                        startActivity(Intent(this, SelectLocationActivity::class.java).putExtra("isView",true))
                    }
                    val list = ArrayList<ParkingImage>()
                    it?.child("images")?.children?.forEach { image ->
                        image.getValue(ParkingImage::class.java)?.let { it1 -> list.add(it1) }
                    }
                    var reserved = 0
                    var me = 0
                    it?.child("booked")?.children?.forEach { book ->
                        book.children.forEach { book2 ->
                            val parkingBooked = book2.getValue(ParkingBooked::class.java)
                            if (parkingBooked?.data?.day != Date().day
                            ) {
                                /*
                                * || (parkingBooked?.data?.hours != Date().hours && (parkingBooked?.data?.minutes
                                    ?: 0) > 40 && (parkingBooked.status ?: 0) > 1)*/

                                parkingViewModel.deleteBookParking(uid, parkingBooked)
                            } else {
                                if (parkingBooked.userId == FirebaseAuth.getInstance().currentUser?.uid && parkingBooked.status != BookedStatus.finishParking){
                                    me = parkingBooked.number?:0
                                    Constants.parkingBooked = parkingBooked
                                    Constants.selectParking = parking
                                }else {
                                    reserved += (parkingBooked.number ?: 0)
                                }
                            }
                            //  Toast.makeText(this, "$reserved", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Toast.makeText(this, "$reserved $me", Toast.LENGTH_SHORT).show()
                    parking?.reserved = reserved
                    binding.available.text =
                        "${(parking?.slotsCount ?: 0) - (parking?.reserved ?: 0)} Slots available"
                    adapterSlider.addImages(list)
                    binding.progress.visibility = View.GONE
                    slotAdapter.setSize(parking?.slotsCount, reserved,me)
                }
            }
        }
    }
}