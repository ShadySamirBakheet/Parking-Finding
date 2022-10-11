package com.apps.parkingfinding.views.home


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.apps.parkingfinding.R
import com.apps.parkingfinding.data.models.User
import com.apps.parkingfinding.databinding.ActivitySplashBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.viewmodel.UserViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.in_right)
        animation.duration = 2000
        binding.icon.startAnimation(animation)
        Handler().postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                if (currentUser.email == "admin@admin.com") {
                    Constants.isAdmin = true
                    Constants.user = User(
                        currentUser.uid,"Admin",currentUser.email,"0",true
                    )
                } else {
                    val liveData = userViewModel.getUserData(currentUser.uid)
                    liveData.observe(this) { dataSnapshot ->
                        if (dataSnapshot != null) {
                            try {
                                Constants.user = dataSnapshot.getValue(User::class.java)
                            } catch (e: Exception) {
                                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                                Log.e("this", e.message!!)
                            }
                        }
                    }
                }
                startActivity(Intent(this, HomeActivity::class.java))
            }
            finish()
        }, 3000)
    }
}