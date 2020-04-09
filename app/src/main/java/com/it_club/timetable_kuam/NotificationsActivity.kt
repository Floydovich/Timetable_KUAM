package com.it_club.timetable_kuam

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.it_club.timetable_kuam.adapters.NotificationsAdapter
import com.it_club.timetable_kuam.model.NotificationItem
import com.it_club.timetable_kuam.model.NotificationsManager
import com.it_club.timetable_kuam.utils.MODE
import com.it_club.timetable_kuam.utils.USER_FILE
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        sharedPreferences = getSharedPreferences(USER_FILE, MODE)

        val data = intent.extras?.getBundle("FCM_MSG")

        if (data != null) {
            val body = data.get("text")
            NotificationsManager.notifications.add(NotificationItem("a title", body.toString()))

            val jsonString = Gson().toJson(NotificationsManager.notifications)
            val editor = sharedPreferences.edit()
            editor.putString("NOTIFICATIONS", jsonString)
            editor.apply()  // saves new message in the background
        }

        recyclerViewNotifications.layoutManager = LinearLayoutManager(this)
        recyclerViewNotifications.adapter = NotificationsAdapter(NotificationsManager.notifications)
    }
}
