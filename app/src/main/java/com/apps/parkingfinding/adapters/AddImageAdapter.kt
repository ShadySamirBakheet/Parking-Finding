package com.apps.parkingfinding.adapters

import android.content.Context
import android.net.Uri

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.apps.parkingfinding.R

class AddImageAdapter(private val context: Context?) :

    RecyclerView.Adapter<AddImageAdapter.ViewHolder>() {
    private var size = 1
    var data: ArrayList<Uri> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_add_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            if (position == size-1) {
                delItem.visibility = View.GONE
                itemImage.setImageResource(R.drawable.image)
                itemImage.setOnClickListener {
                    onItemAddListener.let {
                        if (it != null) {
                            it()
                        }
                    }
                }
            } else {
                delItem.visibility = View.VISIBLE
                itemImage.setImageURI(data[position ])
                delItem.setOnClickListener {
                    data.removeAt(position)
                    size--
                    notifyDataSetChanged()
                }
            }
        }
    }


    private var onItemAddListener: (() -> Unit)? = null

    fun setOnAddListener(listener: () -> Unit) {
        onItemAddListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var delItem: ImageView = itemView.findViewById(R.id.delItem)
        var itemImage: ImageView = itemView.findViewById(R.id.itemImage)
    }

    override fun getItemCount(): Int {
        return size
    }

    fun setImageURI(uri: Uri) {
         data.add(uri)
        size = data.size+1
        notifyDataSetChanged()
    }
}