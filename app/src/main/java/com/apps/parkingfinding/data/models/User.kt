package com.apps.parkingfinding.data.models

import android.content.SharedPreferences

data class User(
    val id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var isAdmin: Boolean? = null,
) {
    constructor(shared: SharedPreferences) : this(
        id = shared.getString("id", ""),
        name = shared.getString("name", ""),
        email = shared.getString("email", ""),
        phone = shared.getString("phone", ""),
        isAdmin = shared.getBoolean("admin", false),
    )
}