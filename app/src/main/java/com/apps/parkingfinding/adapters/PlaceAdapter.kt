package com.apps.parkingfinding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.apps.parkingfinding.R
import com.apps.parkingfinding.data.models.Parking
import com.apps.parkingfinding.utils.Constants
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.intellij.lang.annotations.Language
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class PlaceAdapter(private val context: Context?,val supportFragmentManager: FragmentManager) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    var data: ArrayList<Parking>? = null
    var size = 0
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val parking = data?.get(position)
        holder.apply {
            if (Constants.isAdmin  ){
                delete.visibility = View.VISIBLE
            }else{
                delete.visibility = View.GONE
            }
            if (parking != null) {
                if (context != null) {
                    name.text = parking.name
                    price.text = "${
                        distanceInKm(
                            parking.locationLat,
                            parking.locationLng,
                        )
                    }km -  ${parking.slotsPrice}/h KWD"

                }
                itemView.setOnClickListener {
                    onItemClickListener.let {
                        if (it != null) {
                            it(parking.id ?: "", parking.uid ?: "")
                        }
                    }
                }
                delete.setOnClickListener {
                    Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show()
                    onItemDeleteListener.let {
                        if (it != null) {
                            data?.remove(parking)
                            notifyDataSetChanged()
                            it(parking)
                        }
                    }
                }
            }

        }
    }


    private var onItemClickListener: ((String, String) -> Unit)? = null

    fun setOnClickListener(listener: (String, String) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemDeleteListener: ((Parking) -> Unit)? = null

    fun setOnDeleteListener(listener: (Parking) -> Unit) {
        onItemDeleteListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var delete: ImageView = itemView.findViewById(R.id.delete)
        var name: TextView = itemView.findViewById(R.id.name)
        var price: TextView = itemView.findViewById(R.id.price)


    }

    override fun getItemCount(): Int {
        return size
    }

    fun addData(data: ArrayList<Parking>?, longitude: Double, latitude: Double) {
        this.data = data
        size = data?.size ?: 0
        this.longitude = longitude
        this.latitude = latitude
        notifyDataSetChanged()
    }

    private fun distanceInKm(lat1: Double, lon1: Double, ): Int {
        val theta = lon1 - longitude
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(latitude)) + cos(deg2rad(lat1)) * cos(deg2rad(latitude)) * cos(
                deg2rad(theta)
            )
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60.times(1.1515)
        dist *= 1.609344
        return dist.roundToInt()
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}