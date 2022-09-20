package com.apps.parkingfinding.views.home

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.apps.parkingfinding.R
import com.apps.parkingfinding.databinding.ActivityMainBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.views.auth.SignInActivity
import com.apps.parkingfinding.views.parking.ParkingDetailsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.in_right)
        animation.duration = 2000
        binding.icon.startAnimation(animation)
        binding.loginUser.setOnClickListener {
            Constants.isAdmin= false
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.loginAdmin.setOnClickListener {
            Constants.isAdmin= true
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }
}