package com.apps.parkingfinding.views.purchase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apps.parkingfinding.databinding.ActivityPurchaseBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.views.home.HomeActivity

class PurchaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPurchaseBinding

    var hours = 0
    var price = 0.0
    var priceAll = 0.0
    var clean = false
    var isWallet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hours = intent.getIntExtra("hours", 0)
        price = intent.getDoubleExtra("price", 0.0)
        priceAll = intent.getDoubleExtra("priceAll", 0.0)
        clean = intent.getBooleanExtra("clean", false)
        isWallet = intent.getBooleanExtra("isWallet", false)

        val cleanPrice: Double
        if (clean) cleanPrice = 1.5 else cleanPrice = 0.0


        if (Constants.user?.wallet == null && Constants.user?.wallet ?: 0 == 0 || isWallet) {
            binding.wallet.visibility = View.GONE
        }else{
            binding.wallet.visibility = View.VISIBLE
            binding.wallet.text = "Pay from my Wallet ${Constants.user?.wallet ?: 0} KWD"
            binding.wallet
                .setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        binding.knet.visibility = View.GONE
                    } else {
                        binding.knet.visibility = View.VISIBLE
                    }
                }
        }



        binding.total.text = "$priceAll KWD"
        Toast.makeText(
            this,
            "${(hours * price) + cleanPrice} KWD $priceAll $cleanPrice  $clean",
            Toast.LENGTH_SHORT
        ).show()

        binding.back.setOnClickListener {
            finish()
        }
        binding.confirm.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}