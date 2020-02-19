package com.example.timetable_kuam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.timetable_kuam.adapters.DaysAdapter
import com.example.timetable_kuam.model.ClassItem
import com.example.timetable_kuam.utils.FILE_PATH
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtil
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timetable = parseJson(intent.getStringExtra(FILE_PATH)!!)

        val daysAdapter = DaysAdapter(timetable, this)
        viewPager.adapter = daysAdapter
        viewPager.offscreenPageLimit = 3  // загружает по 3 страницы слева и справа от текущей
        viewPager.setCurrentItem(setPageOnToday(), false)

        attachTabs()
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
                else -> "unwanted tab"
            }
        } .attach()
    }

    private fun parseJson(jsonFile: String): List<ClassItem> {
        return Gson().fromJson(
            IOUtil.toString(assets.open(jsonFile)),
            object : TypeToken<List<ClassItem>>() {}.type
        )
    }

    private fun setPageOnToday(): Int {
        var today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2  // т.к. по ум. Сб = 0

        if (today < 0) today += 7

        return today
    }
}
