package com.apps.parkingfinding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apps.parkingfinding.R
import com.apps.parkingfinding.utils.Constants

class SlotAdapter(private val context: Context?) :
    RecyclerView.Adapter<SlotAdapter.ViewHolder>() {

    private var size = 0
    var reserved = ArrayList<Int>()
    var me = ArrayList<Int>()

    val reserveds = ArrayList<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_slot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val num = position + 1
        holder.apply {
            if (reserved.contains(num)) {
                itemImage.setImageResource(R.drawable.carr)
            } else {

                itemImage.setImageResource(R.drawable.carg)
                if (reserveds.contains(num)||me.contains(num)) {
                    itemImage.setImageResource(R.drawable.carb)
                }
                if (!Constants.isAdmin) {
                    itemView.setOnClickListener {
                        itemImage.setImageResource(R.drawable.carb)
                        reserveds.add(num)
                    }
                }

            }
            number.text = num.toString()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var number: TextView = itemView.findViewById(R.id.number)
        var itemImage: ImageView = itemView.findViewById(R.id.itemImage)
    }

    override fun getItemCount(): Int {
        return size
    }

    fun setSize(slotsCount: Int?, reserved:  ArrayList<Int>, me:  ArrayList<Int>) {
        size = slotsCount ?: 0
        this.reserved = reserved
        reserveds.clear()
        this.me.addAll(me)
        reserveds.addAll(me)
//        Toast.makeText(context, "size $slotsCount res $reserved me $me", Toast.LENGTH_SHORT).show()
        notifyDataSetChanged()
    }



}