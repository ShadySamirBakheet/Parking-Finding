package com.apps.parkingfinding.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Methods {
    private val sdfDate = SimpleDateFormat("dd MMM yyyy")
    private val sdfTime = SimpleDateFormat("hh:mm")
    fun  getMessageId(id1:String,id2:String):String{
        return  if (id1>id2){
            "$id1-$id2"
        }else{
            "$id2-$id1"
        }
    }

    fun getDateString(date: Date): String {
        return sdfDate.format(date)
    }
    fun getTimeString(date: Date): String {
        return sdfTime.format(date)
    }


     fun getAddress(context: Context?, lat: Double, lng: Double): String? {
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
        return try {
            val addresses: List<Address>? = geocoder?.getFromLocation(lat, lng, 1)
            val obj: Address = addresses!![0]
            obj.getAddressLine(0)
        } catch (e: IOException) {
            e.printStackTrace()
            //Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            null
        }
    }
}