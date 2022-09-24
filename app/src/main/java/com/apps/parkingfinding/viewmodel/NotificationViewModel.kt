package com.apps.parkingfinding.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.apps.parkingfinding.data.datasources.FirebaseQueryLiveData
import com.apps.parkingfinding.data.models.Notification
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class NotificationViewModel (application: Application): AndroidViewModel(application){

    private val notificationRef = FirebaseDatabase.getInstance().getReference("notification/")

    private val liveData = FirebaseQueryLiveData(notificationRef)

    fun getNotificationData(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun getNotificationData(id:String,toUid:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(notificationRef.child("${toUid}/${id}"))
    }

    fun getNotificationData(toUid:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(notificationRef.child("$toUid"))
    }

    fun setNotificationData(notification: Notification): Task<Void> {
        return notificationRef.child("${notification.toUid}/${notification.id}").setValue(notification)
    }
}