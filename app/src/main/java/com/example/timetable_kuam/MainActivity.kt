package com.example.timetable_kuam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.timetable_kuam.adapters.DaysAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtil
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val daysAdapter = DaysAdapter(IOUtil.toString(assets.open("timetable.json")), this)

        viewPager.adapter = daysAdapter

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK) - 2 // Суб = индекс 7
        // Устанавливает страницу на текущий день недели
        viewPager.currentItem = today

        attachTabs()
    }

    // Присоединяем вкладки
    private fun attachTabs() {
        TabLayoutMediator(tabs, viewPager) {tab, position ->
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
