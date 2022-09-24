package com.apps.parkingfinding.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.apps.parkingfinding.data.datasources.FirebaseQueryLiveData
import com.apps.parkingfinding.data.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase


class UserViewModel (application: Application): AndroidViewModel(application){

    private val userRef = FirebaseDatabase.getInstance().getReference("users/")

    private val liveData = FirebaseQueryLiveData(userRef)

    fun getUsersData(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun getUserData(id:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(userRef.child(id))
    }

    fun setUserData(user: User): Task<Void> {
        return userRef.child(user.id.toString()).setValue(user)
    }

}