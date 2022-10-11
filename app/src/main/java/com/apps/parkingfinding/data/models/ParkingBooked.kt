package com.apps.parkingfinding.data.models

import java.util.*
import kotlin.collections.ArrayList

data class ParkingBooked(
    val id: String? = null,
    var parkingId: String? = null,
    var userId: String? = null,
    var data: Date? = null,
    var number: ArrayList<Int>?=null,
    var status:Int?=0,
    var isClean:Int=0,
)


