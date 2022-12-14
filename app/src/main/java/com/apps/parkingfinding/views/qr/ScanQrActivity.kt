package com.apps.parkingfinding.views.qr

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.apps.parkingfinding.R
import com.apps.parkingfinding.data.models.BookedStatus
import com.apps.parkingfinding.data.models.Notification
import com.apps.parkingfinding.databinding.ActivityScanQrBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.viewmodel.NetworkViewModel
import com.apps.parkingfinding.viewmodel.NotificationViewModel
import com.apps.parkingfinding.viewmodel.ParkingViewModel
import com.apps.parkingfinding.views.home.HomeActivity
import com.apps.parkingfinding.views.purchase.PurchaseActivity
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import java.util.*


class ScanQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrBinding


    private lateinit var parkingViewModel: ParkingViewModel
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""

    var isEnter = false
    var hours = 0
    var timer = 0
    var clean:Double = 0.0

    var handler: Handler? = Handler()
    var stop = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        parkingViewModel = ViewModelProvider(this)[ParkingViewModel::class.java]
        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        if (Constants.parkingBooked?.isClean?: 0 >0){
            binding.clean.visibility = View.VISIBLE
            clean = 1.5
        }else{
            clean = 0.0
            binding.clean.visibility = View.GONE
        }

        isEnter = intent.getBooleanExtra("isEnter", false)

        binding.confirm.isEnabled = false
        if (isEnter) {
            timer = intent.getIntExtra("timer", 0)
            handler?.postDelayed(object : Runnable {
                override fun run() {
                    timer--
                    binding.msg.text = "You have ${timer} Sec to scan QR code at the Parking"
                    if (timer > 0 ) {
                        if ( !stop){
                            handler?.postDelayed(this, 1000)
                        }
                    } else {
                        deleteParking()
                        startActivity(Intent(this@ScanQrActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }, 1000)
            binding.timer.visibility = View.GONE
            binding.cal.visibility = View.GONE
            binding.msg.text = "You have 59 Sec to scan QR code at the Parking"
            binding.confirm.text = "Confirm"
            binding.confirm.setOnClickListener {
                stop = true
                Constants.parkingBooked?.status = BookedStatus.inParking
                Constants.parkingBooked?.let { it1 ->
                    binding.progress.visibility = View.VISIBLE

                    parkingViewModel.bookParking(
                        Constants.selectParking?.uid.toString(),
                        it1

                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            notificationViewModel.setNotificationData(
                                Notification(
                                    Constants.user?.name.toString(),
                                    "I am in Parking",
                                    System.currentTimeMillis().toString(),
                                    "Admin",
                                    Date(),
                                ),
                            )
                            notificationViewModel.setNotificationData(
                                Notification(
                                    "Admin",
                                    "you are in Parking",
                                    System.currentTimeMillis().toString(),
                                    Constants.user?.id.toString(),
                                    Date(),
                                ),
                            )
                        }
                        binding.progress.visibility = View.GONE

                        finish()
                    }
                }
            }
        } else {
            binding.timer.visibility = View.VISIBLE
            binding.cal.visibility = View.VISIBLE
            binding.msg.text = "You left us now"
            binding.confirm.text = "Go to Purchase"
            binding.confirm.setOnClickListener {
                Constants.parkingBooked?.status = BookedStatus.finishParking
                Constants.parkingBooked?.let { it1 ->
                    binding.progress.visibility = View.VISIBLE



                    if (Constants.parkingBooked?.isClean?: 0 >0){
                        binding.clean.visibility = View.VISIBLE
                        clean = 1.5
                    }else{
                        clean = 0.0
                        binding.clean.visibility = View.GONE
                    }

                    parkingViewModel.bookParking(
                        Constants.selectParking?.uid.toString(),
                        it1
                    ).addOnCompleteListener {
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
                            notificationViewModel.setNotificationData(
                                Notification(
                                    "Admin",
                                    "you are leave Parking",
                                    System.currentTimeMillis().toString(),
                                    Constants.user?.id.toString(),
                                    Date(),
                                ),
                            )
                        }
                        startActivity(
                            Intent(this, PurchaseActivity::class.java).putExtra(
                                "hours",
                                hours
                            ).putExtra("price", Constants.selectParking?.slotsPrice ?: 0.0 )
                            .putExtra("priceAll",(Constants.selectParking?.slotsPrice?:0.0)*hours.toDouble() +clean)
                        )
                        Constants.parkingBooked = null
                        Constants.selectParking = null
                        binding.progress.visibility = View.GONE

                        finish()
                    }
                }
            }

        }

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }

        binding.back.setOnClickListener {
            deleteParking()
            finish()
        }

        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)
    }


    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        binding.cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue


                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    runOnUiThread {
                        stop = true
                        binding.barcodeLine.clearAnimation()
                        cameraSource.stop()
//                        Toast.makeText(
//                            this@ScanQrActivity,
//                            "value- $scannedValue",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        binding.confirm.isEnabled = true
                        if (!isEnter) {
                            val hour = Date().hours - (Constants.parkingBooked?.data?.hours ?: 0)
                            val diff: Long =
                                Date().time - (Constants.parkingBooked?.data?.time ?: Date().time)
                            val seconds = diff / 1000
                            val minutes = seconds / 60
                            hours = (minutes / 60).toInt()

                            val minInHour = minutes % 60

                            if (minInHour > 30 && hours > 0) {
                                hours++
                            } else if (minInHour >= 0 && hours.toInt() == 0) {
                                hours++
                            }
                            binding.timer.text = "$hours hours" //4h ?? 3$
                            binding.euq.text =
                                "$hours h ?? ${Constants.selectParking?.slotsPrice ?: 0} KWD"
                            binding.price.text =
                                "${(Constants.selectParking?.slotsPrice ?: 0.0) * hours} KWD"
                        }
                        // finish()
                    }
                }
            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        deleteParking()
        super.onBackPressed()
    }



    private fun deleteParking() {
        if (Constants.selectParking != null) {
            parkingViewModel.deleteBookParking(
                Constants.selectParking?.uid ?: "",
                Constants.parkingBooked
            )
            notificationViewModel.setNotificationData(
                Notification(
                    Constants.user?.name.toString(),
                    "I am delete book Parking",
                    System.currentTimeMillis().toString(),
                    "Admin",
                    Date(),
                ),
            )
            notificationViewModel.setNotificationData(
                Notification(
                    "Admin",
                    "you are delete book Parking",
                    System.currentTimeMillis().toString(),
                    Constants.user?.id.toString(),
                    Date(),
                ),
            )
            Constants.parkingBooked = null
            Constants.selectParking = null
            finish()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }
}