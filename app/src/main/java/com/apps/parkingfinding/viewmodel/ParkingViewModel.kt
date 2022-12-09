package com.apps.parkingfinding.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.apps.parkingfinding.data.datasources.FirebaseQueryLiveData
import com.apps.parkingfinding.data.models.Parking
import com.apps.parkingfinding.data.models.ParkingBooked
import com.apps.parkingfinding.data.models.ParkingImage
import com.apps.parkingfinding.utils.FileUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        var location: LiveData<LatLng?>? = null
        var locationName: LiveData<String?>? = null
    }


    fun selLocation(latLng: LatLng, name: String) {
        location = liveData {
            emit(latLng)
        }
        locationName = liveData {
            emit(name)
        }
    }

    private val parkingRef = FirebaseDatabase.getInstance().getReference("parking/")

    private val liveData = FirebaseQueryLiveData(parkingRef)

    fun getParkingData(): LiveData<DataSnapshot?> {
        return liveData

    }

    fun getSelectParking(id: String, uid: String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(parkingRef.child(uid).child(id))
    }

    fun setParkingDate(parking: Parking): Task<Void> { return parkingRef.child(parking.uid + "/" + parking.id).setValue(parking)
    }

    fun bookParking(uid: String, parkingBooked: ParkingBooked): Task<Void> {
        return parkingRef.child(uid + "/" + parkingBooked.parkingId + "/booked/${parkingBooked.userId}/" + parkingBooked.id)
            .setValue(parkingBooked)
    }

    fun clearLocation() {
        location = liveData {
            emit(null)
        }
        locationName = liveData {
            emit(null)
        }
    }

    fun saveParkingImage(images: ArrayList<Uri>, id: String, uid: String, context: Context) {

        if (images.isNotEmpty()) {
            for (fileSel in images) {
                val parkingImage = ParkingImage(
                    id = System.currentTimeMillis().toString(),
                    id,
                )
                var file2 = File(FileUtils.getSmartFilePath(context, fileSel) ?: "")
                GlobalScope.launch {
                    file2 = Compressor.compress(context, file2) {
                        default(format = Bitmap.CompressFormat.PNG)
                    }
                }
                val ref2 =
                    FirebaseStorage.getInstance().reference.child("parking_images")
                        .child(parkingImage.id + "Images" + ".png")
                ref2.putFile(file2.toUri()).addOnCompleteListener {
                    it.result!!.storage.downloadUrl.addOnSuccessListener { uri ->
                        parkingImage.image = uri.toString()

                        parkingRef.child(uid + "/" + id + "/images/" + parkingImage.id)
                            .setValue(parkingImage).addOnCompleteListener {

                        }


                    }
                }
            }
        }

    }

    fun deleteBookParking(uid: String, parkingBooked: ParkingBooked?): Task<Void> {
        return parkingRef.child(uid + "/" + parkingBooked?.parkingId + "/booked/${parkingBooked?.userId}/" + parkingBooked?.id)
            .removeValue()

    }
    fun deleteImagParking(uid: String, parkingBooked: ParkingImage?): Task<Void> {
        return parkingRef.child(uid + "/" + parkingBooked?.parkingId + "/images/" + parkingBooked?.id)
            .removeValue()

    }

    fun deleteParking(parking: Parking): Task<Void> {
        return parkingRef.child(parking.uid + "/" + parking.id).removeValue()
    }

}