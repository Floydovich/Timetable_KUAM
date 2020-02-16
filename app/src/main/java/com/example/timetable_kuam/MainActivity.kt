package com.example.timetable_kuam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.timetable_kuam.adapters.DaysAdapter
import com.example.timetable_kuam.utils.ARG_JSON
import com.example.timetable_kuam.utils.FILE_PATH
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtil
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jsonFile = intent.getStringExtra(FILE_PATH)

        if (jsonFile != null) {
            val daysAdapter = DaysAdapter(IOUtil.toString(assets.open(jsonFile)), this)

            viewPager.adapter = daysAdapter

            val calendar = Calendar.getInstance()
            var today = calendar.get(Calendar.DAY_OF_WEEK) - 2

            if (today == -1) today = 6 // Ставит на воскресенье

            viewPager.currentItem = today

            attachTabs()

        } else {
            startActivity(Intent(this, GroupSelectionActivity::class.java))
        }
    }

    private fun attachTabs() {
        TabLayoutMediator(tabs, viewPager) {tab, position ->
            tab.text = when(position) {
                0 -> "пн"
                1 -> "вт"
                2 -> "ср"
                3 -> "чт"
                4 -> "пт"
                5 -> "сб"
                6 -> "вс"
                else -> "Empty tab."
            }
        }.attach()
    }
}
