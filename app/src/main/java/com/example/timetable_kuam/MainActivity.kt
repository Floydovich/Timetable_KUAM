package com.example.timetable_kuam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.timetable_kuam.model.ClassItem
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

        // Парсит JSON в классы и отправляет их адаптеру
        val daysAdapter = DaysAdapter(parseClasses("timetable.json"), this)
        viewPager.adapter = daysAdapter

        // Создаёт инстанс календаря чтобы брать оттуда день недели и прочее время
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK) - 2  // Вс имеет индекс 1
        // Устанавливает страницу на текущий день недели
        viewPager.currentItem = today

        attachTabs()
    }

    private fun parseClasses(jsonFile: String) = Gson()
        .fromJson<List<ClassItem>>(
            // Открывает файл и переводит его в строку
            IOUtil.toString(assets.open(jsonFile), "UTF-8"),
            // Тут задаётся тип листа, куда будет парсится JSON
            object : TypeToken<List<ClassItem>>() {}.type
        )

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
