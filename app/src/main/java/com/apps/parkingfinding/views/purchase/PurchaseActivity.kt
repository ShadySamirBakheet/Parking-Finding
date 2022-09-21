package com.apps.parkingfinding.views.purchase

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apps.parkingfinding.databinding.ActivityPurchaseBinding
import com.apps.parkingfinding.views.home.HomeActivity

class PurchaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPurchaseBinding

    var hours =0
    var price =0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hours= intent.getIntExtra("hours",0)
        price= intent.getDoubleExtra("price",0.0)

        binding.total.text = "${hours+price} KWD"

        binding.back.setOnClickListener {
            finish()
        }
        binding.confirm.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
    }
}