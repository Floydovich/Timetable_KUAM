package com.it_club.timetable_kuam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.it_club.timetable_kuam.adapters.NotificationsAdapter
import com.it_club.timetable_kuam.model.NotificationItem
import com.it_club.timetable_kuam.model.NotificationsManager
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notifications)

        recyclerViewNotifications.layoutManager = LinearLayoutManager(this)
        recyclerViewNotifications.adapter = NotificationsAdapter(NotificationsManager.notifications)
    }

//
//    override fun onResume() {
//        super.onResume()
//
//        recyclerViewNotifications.adapter?.notifyDataSetChanged()
//    }
}
