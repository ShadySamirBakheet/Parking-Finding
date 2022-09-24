package com.apps.parkingfinding.data.models

import java.util.*

data class ParkingBooked(
    val id: String? = null,
    var parkingId: String? = null,
    var userId: String? = null,
    var data: Date? = null,
    var number: Int?=null,
    var status:Int?=0,
)


