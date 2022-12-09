package com.apps.parkingfinding.views.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.apps.parkingfinding.R
import com.apps.parkingfinding.databinding.ActivityProfileBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.viewmodel.UserViewModel
import com.apps.parkingfinding.views.purchase.PurchaseActivity
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.back.setOnClickListener {
            finish()
        }

        binding.wallet.text = "Wallet : ${Constants.user?.wallet ?: 0} KWD"
        binding.name.text = "Name : ${Constants.user?.name}"
        binding.email.text = "Email : ${Constants.user?.email}"
        binding.phone.text = "Phone : ${Constants.user?.phone}"

        binding.walletLay.setOnClickListener {
            chargeDialog()
        }

    }


    private fun chargeDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        val alertDialog = alertDialogBuilder.create()
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(
            R.layout.popup_add_wallet, null
        )
        alertDialog.setView(dialogLayout)

        dialogLayout.findViewById<TextView>(R.id.charge).setOnClickListener {

            var wallet = dialogLayout.findViewById<TextInputEditText>(R.id.wallet)
            Constants.user?.wallet =
                (Constants.user?.wallet ?: 0) + (wallet.text.toString().toIntOrNull() ?: 0)
            binding.wallet.text = "Wallet : ${Constants.user?.wallet ?: 0} KWD"

            if (Constants.user?.wallet != null && Constants.user?.wallet ?: 0 > 0) {
                userViewModel.setUserData(Constants.user!!)
                startActivity(
                    Intent(this, PurchaseActivity::class.java)
                        .putExtra("priceAll",(wallet.text.toString().toIntOrNull() ?: 0).toDouble())
                        .putExtra("isWallet",true)
                )
                finish()
                alertDialog.dismiss()

            }else{
                Toast.makeText(this, "Please Fill Field", Toast.LENGTH_SHORT).show()
            }
        }


        alertDialog.show()
    }

}