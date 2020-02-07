package com.example.timetable_kuam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.timetable_kuam.model.Class_
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: Список дней из timetable.json

        val daysAdapter = DaysAdapter(ArrayList<Class_>(), this)
        viewPager.adapter = daysAdapter

        attachTabs()
    }

    private fun attachTabs() {
        TabLayoutMediator(tabs, viewPager) {tab, position ->
            // Количество when должно быть равно getItemCount адаптера
            tab.text = when(position) {
                0 -> "пн"
                1 -> "вт"
                2 -> "ср"
                3 -> "чт"
                4 -> "пт"
                5 -> "сб"
                else -> "вс"
            }
        }.attach()
    }
}
