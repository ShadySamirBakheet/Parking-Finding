package com.apps.parkingfinding.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

object PermissionCheck {
    fun readAndWriteExternalStorage(context: Context?): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
            false
        } else {
            true
        }
    }

    fun audioRecord(context: Context?): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                2
            )
            false
        } else {
            true
        }
    }

    fun readAndWriteContacts(context: Context?): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                3
            )
            false
        } else {
            true
        }
    }

    fun vibrate(context: Context?): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.VIBRATE),
                4
            )
            false
        } else {
            true
        }
    }

    fun sendSms(context: Context?): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.SEND_SMS),
                5
            )
            false
        } else {
            true
        }
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    //Just like this you can implement rest of the permissions.
    fun checkAndRequestPermissions(context: Context?, activity: Activity?, code: Int): Boolean {
        val camerapermission =
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
        val writepermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionLocation =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionRecordAudio =
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                activity!!,
                listPermissionsNeeded.toTypedArray(),
                code
            )
            return false
        }
        return true
    }
}
