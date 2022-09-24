package com.apps.parkingfinding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apps.parkingfinding.R
import com.apps.parkingfinding.data.models.Notification
import java.util.ArrayList

class NotificationAdapter (private val context: Context?) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    var data =  ArrayList<Notification>(

    )
    var size = 0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val item = data[position]
            date.text = item.date.toString()
            userName.text = item.userName.toString()
            desc.text = item.msg.toString()
        }
    }


    private var onItemClickListener: ((String,String) -> Unit)? = null

    fun setOnSaveListener(listener: (String,String) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById(R.id.userName)
        var date: TextView = itemView.findViewById(R.id.date)
        var desc: TextView = itemView.findViewById(R.id.desc)
    }

    override fun getItemCount(): Int {
        return size
    }

    fun addData(list: ArrayList<Notification>) {
        data = list
        size = data.size
        data.reverse()
        notifyDataSetChanged()
    }
}