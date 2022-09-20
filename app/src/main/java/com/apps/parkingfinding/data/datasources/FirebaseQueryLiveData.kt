package com.apps.parkingfinding.data.datasources

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.*

class FirebaseQueryLiveData : LiveData<DataSnapshot?> {

    private val query: Query
    private val listener: MyValueEventListener = MyValueEventListener()

    constructor(query: Query) {
        this.query = query
    }

    @SuppressLint("RestrictedApi")
    constructor(ref: DatabaseReference) {
        query = ref
        Log.d(LOG_TAG, ref.path.toString())
    }

    override fun onActive() {
        Log.d(LOG_TAG, "onActive")
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        Log.d(LOG_TAG, "onInactive")
        query.removeEventListener(listener)
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()){
                Log.d(LOG_TAG, dataSnapshot.key+" have "+ dataSnapshot.childrenCount.toString())
                value = dataSnapshot
            }else{
                Log.d(LOG_TAG, "Empty")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(
                LOG_TAG,
                "Can't listen to query $query", databaseError.toException()
            )
        }
    }

    companion object {
        private const val LOG_TAG = "FirebaseQueryLiveData"
    }
}