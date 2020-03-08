package com.it_club.timetable_kuam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.it_club.timetable_kuam.adapters.NotificationsAdapter
import com.it_club.timetable_kuam.model.NotificationItem
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notifications)

        recyclerViewNotifications.layoutManager = LinearLayoutManager(this)
        recyclerViewNotifications.adapter = NotificationsAdapter(listOf(
            NotificationItem("ИС-32", "В четверг пара СУБД была перенесена в 306 кабинет.", "Четверг", "31 марта"),
            NotificationItem("ИС-32", "В пятницу коронавирус убил всех. Заниятия отменяются.", "Пятница", "1 апреля")
        ))
    }

//
//    override fun onResume() {
//        super.onResume()
//
//        recyclerViewNotifications.adapter?.notifyDataSetChanged()
//    }
}
