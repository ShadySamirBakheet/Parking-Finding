package com.apps.parkingfinding.views.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apps.parkingfinding.R
import com.apps.parkingfinding.databinding.ActivityAboutBinding
import com.apps.parkingfinding.databinding.ActivityPurchaseBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

    }
}