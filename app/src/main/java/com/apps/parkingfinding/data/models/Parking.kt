package com.apps.parkingfinding.data.models

data class Parking(
    var uid :String?=null,
    var id :String?=null,
    var name:String?=null,
    var slotsCount:Int=0,
    var reserved:Int=0,
    var slotsPrice:Double=0.0,
    var  locationLat:Double=0.0,
    var  locationLng:Double=0.0,
    var  locationName:String?=null,
)
