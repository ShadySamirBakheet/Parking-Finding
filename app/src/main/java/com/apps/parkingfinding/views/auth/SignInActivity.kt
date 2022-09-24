package com.apps.parkingfinding.views.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.apps.parkingfinding.data.datasources.SharedStorage
import com.apps.parkingfinding.data.models.User
import com.apps.parkingfinding.databinding.ActivitySignInBinding
import com.apps.parkingfinding.utils.Constants.isAdmin
import com.apps.parkingfinding.viewmodel.NetworkViewModel
import com.apps.parkingfinding.viewmodel.UserViewModel
import com.apps.parkingfinding.views.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding


    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        if (isAdmin) {
            binding.signUp.visibility = View.GONE
        }


        binding.signIn.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            networkViewModel.networkState(this).observe(this) {
                binding.progress.visibility = View.GONE
                if (it) {
                    if (isAdmin) {
                        signAdmin()
                    } else {
                        signIn()
                    }

                } else {
                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.signUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signAdmin() {
        val userEmail = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (userEmail.isNotEmpty() && password.isNotEmpty() && userEmail.lowercase(Locale.getDefault()) == "admin@admin.com" && password == "adminadmin") {
            binding.progress.visibility = View.VISIBLE
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(userEmail, password)
                .addOnSuccessListener {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Error Data or Not Found User ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.e("firebase","Error Data or Not Found User ${it.message}")
                }

        } else {
            Toast.makeText(this, "Error Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn() {
        val userEmail = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        binding.progress.visibility = View.VISIBLE

        if (userEmail.isNotEmpty() && password.isNotEmpty()) {

            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(userEmail, password)
                .addOnSuccessListener {

                    val liveData = userViewModel.getUserData(it.user!!.uid)
                    liveData.observe(this) { dataSnapshot ->
                        if (dataSnapshot != null) {
                            var user: User? = null

                            try {
                                user = dataSnapshot.getValue(User::class.java)

                            } catch (e: Exception) {
                                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                                Log.e("this", e.message!!)
                            }
                            if (user != null) {
                                SharedStorage.saveLoginData(this, user)

                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()


                            } else {

                                binding.progress.visibility = View.GONE

                                Toast.makeText(this, "Error Data or Not Found", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }


                        binding.progress.visibility = View.GONE

                        binding.email.setText("")
                        binding.password.setText("")
                    }

                }.addOnFailureListener {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Error Data or Not Found User ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        } else {
            Toast.makeText(this, "Error Data", Toast.LENGTH_SHORT).show()
        }
    }

}