package com.apps.parkingfinding.data.datasources

import android.content.Context
import android.content.SharedPreferences
import com.apps.parkingfinding.data.models.User

class SharedStorage {
    companion object{

        fun saveLoginData(context: Context,user: User){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            edit.putString("id", user.id)
            edit.putString("phone", user.phone)
            edit.putString("email", user.email)
            edit.putString("name", user.name)
            edit.putBoolean("admin", user.isAdmin?:false)
            edit.apply()
        }

        fun getLoginUser(context: Context): User? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            return User(sharedPreferences)
        }

        fun isAdmin(context: Context): Boolean {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("admin",false)
        }

    }
}