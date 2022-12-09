package com.apps.parkingfinding.adapters

import android.content.Context
import android.net.Uri

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.apps.parkingfinding.R
import com.apps.parkingfinding.data.models.ParkingImage
import com.bumptech.glide.Glide

class AddImageAdapter(private val context: Context?) :

    RecyclerView.Adapter<AddImageAdapter.ViewHolder>() {
    private var size = 1
    var data: ArrayList<ParkingImage> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_add_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            if (position == size - 1) {
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
                if (data[position].id != null) {
                    if (context != null) {
                        Glide.with(context).load(data[position].image)
                            .placeholder(R.drawable.image2)
                            .into(itemImage)
                    }
                }else{
                    itemImage.setImageURI(data[position].imageUri)
                }
                delItem.visibility = View.VISIBLE
                delItem.setOnClickListener {
                    if (data[position].id != null) {
                        onItemDeleteListener.let {
                            if (it != null) {
                                it(data[position])
                            }
                        }
                    }
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

    private var onItemDeleteListener: ((ParkingImage) -> Unit)? = null

    fun setOnDeleteListener(listener: (ParkingImage) -> Unit) {
        onItemDeleteListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var delItem: ImageView = itemView.findViewById(R.id.delItem)
        var itemImage: ImageView = itemView.findViewById(R.id.itemImage)
    }

    override fun getItemCount(): Int {
        return size
    }

    fun setImageURI(uri: Uri) {
        data.add(
            ParkingImage(
                imageUri = uri
            )
        )
        size = data.size + 1
        notifyDataSetChanged()
    }

    fun setDataFun(selectParkingImages: ArrayList<ParkingImage>?) {
        selectParkingImages?.forEach {
            data.add((it))
        }
        size = data.size + 1
        notifyDataSetChanged()
    }
}

