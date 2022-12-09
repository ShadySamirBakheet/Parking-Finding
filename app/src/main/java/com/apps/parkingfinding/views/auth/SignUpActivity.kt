package com.apps.parkingfinding.views.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.apps.parkingfinding.data.datasources.SharedStorage
import com.apps.parkingfinding.data.models.User
import com.apps.parkingfinding.databinding.ActivitySignUpBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.viewmodel.NetworkViewModel
import com.apps.parkingfinding.viewmodel.UserViewModel
import com.apps.parkingfinding.views.home.HomeActivity
import com.apps.parkingfinding.views.home.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]



        binding.progress.visibility = View.GONE

        binding.signIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding.signUp.setOnClickListener {

            binding.progress.visibility = View.VISIBLE

            networkViewModel.networkState(this).observe(this) {
                if (it) {
                    signUp()
                } else {

                    binding.progress.visibility = View.GONE

                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signUp() {

        val password = binding.password.text.toString().trim()
        val confirmPassword = binding.confirmPassword.text.toString().trim()

        val userEmail = binding.email.text.toString().trim().lowercase()
        val fullName = binding.userName.text.toString().trim()
        val phone = binding.phone.text.toString().trim()
        binding.progress.visibility = View.VISIBLE

        if (fullName.isNotEmpty() && password.isNotEmpty() &&
            userEmail.isNotEmpty() &&
            confirmPassword.isNotEmpty() && password == confirmPassword
        ){

            auth.createUserWithEmailAndPassword(userEmail,password).addOnSuccessListener {
                it.user?.uid
                val user = User(id=  it.user?.uid, name = fullName,
                    email = userEmail,phone =phone)

                userViewModel.setUserData(user).addOnSuccessListener {
                    SharedStorage.saveLoginData(this,user)
                    Constants.user =user
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }.addOnFailureListener{
                    Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                }

                binding.progress.visibility = View.GONE

            }.addOnFailureListener {

                binding.progress.visibility = View.GONE

                Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }

        }else{
            binding.progress.visibility = View.GONE

            Toast.makeText(this, "Error Data Enter", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        super.onBackPressed()
    }

}