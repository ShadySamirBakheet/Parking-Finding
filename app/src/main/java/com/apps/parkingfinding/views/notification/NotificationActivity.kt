package com.apps.parkingfinding.views.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.parkingfinding.adapters.NotificationAdapter
import com.apps.parkingfinding.adapters.PlaceAdapter
import com.apps.parkingfinding.data.models.Notification
import com.apps.parkingfinding.databinding.ActivityNotificationBinding
import com.apps.parkingfinding.utils.Constants
import com.apps.parkingfinding.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        getNoty()
        binding.back.setOnClickListener {
            finish()
        }
        notificationAdapter=NotificationAdapter(this)
        binding.notificationList.apply {
            setHasFixedSize( true)
            layoutManager = LinearLayoutManager(context)
            adapter= notificationAdapter
        }
    }

    private fun getNoty() {
        var uid = ""
        if (Constants.isAdmin){
            uid = "Admin"
        }
        if (!Constants.isAdmin){
            uid = FirebaseAuth.getInstance().uid?:""
        }
       binding.progress.visibility =View.VISIBLE
        notificationViewModel.getNotificationData(uid).observe(this){
            val list = ArrayList<Notification>()
            it?.children?.forEach{ item->
                item.getValue(Notification::class.java)?.let { it1 ->
                   // Toast.makeText(this, "$it1", Toast.LENGTH_SHORT).show()
                    list.add(it1)
                }
            }
            binding.progress.visibility =View.GONE
            notificationAdapter.addData(list)
        }
    }
}