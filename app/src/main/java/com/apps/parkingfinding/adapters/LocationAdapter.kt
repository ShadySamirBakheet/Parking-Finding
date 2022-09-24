package com.apps.parkingfinding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apps.parkingfinding.R

class LocationAdapter(private val context: Context?, val name: String) :
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.loction.apply {
            text = name
            setOnClickListener {
                onItemDeleteListener.let {

                    if (it != null) {
                        it()
                    }
                }
            }
        }
    }

    private var onItemDeleteListener: (() -> Unit)? = null

    fun setOnDeleteListener(listener: () -> Unit) {
        onItemDeleteListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var loction: TextView = itemView.findViewById(R.id.loction)
    }

    override fun getItemCount(): Int {
        return 1
    }
}