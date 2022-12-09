package com.apps.parkingfinding.data.models

import android.net.Uri

data class ParkingImage(
    val id: String? = null,
    var parkingId: String? = null,
    var image: String? = null,
    var  imageUri: Uri?=null,
)
