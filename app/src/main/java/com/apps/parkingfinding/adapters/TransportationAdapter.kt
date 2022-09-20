package com.apps.parkingfinding.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.apps.parkingfinding.R
import com.apps.parkingfinding.data.models.Transportation

class TransportationAdapter(private val context: Context?) :
    RecyclerView.Adapter<TransportationAdapter.ViewHolder>() {

    var list = arrayListOf(
Transportation("Car",R.drawable.car),
Transportation("Bike",R.drawable.motorbike),
Transportation("Truck",R.drawable.box_truck),
Transportation("Bycicle",R.drawable.bycicle),
    )

    var selectedIndex = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_transportation, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.apply {
            name.text = item.name
            type.setImageResource(item.image)
            if (selectedIndex == position) {
                container.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.blueColor))
            }else{
                container.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.greyColor))
            }
            container.setOnClickListener {
                selectedIndex = position
                notifyDataSetChanged()
            }
        }
    }


    private var onItemClickListener: ((String, String) -> Unit)? = null

    fun setOnSaveListener(listener: (String, String) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var container: CardView = itemView.findViewById(R.id.container)
        var type: ImageView = itemView.findViewById(R.id.type)
        var name: TextView = itemView.findViewById(R.id.name)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}