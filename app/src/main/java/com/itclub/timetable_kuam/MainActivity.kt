package com.itclub.timetable_kuam

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.example.timetable_kuam.R
import com.itclub.timetable_kuam.adapters.DaysAdapter
import com.itclub.timetable_kuam.model.ClassItem
import com.example.timetable_kuam.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtil
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(USER_FILE, MODE)

        val spec = intent.getStringExtra(SPEC_NAME) ?: 
            sharedPreferences.getString(SPEC_NAME, null)
        
        val group = intent.getStringExtra(GROUP_NAME) ?:
            sharedPreferences.getString(GROUP_NAME, null)

        if (group != null && spec != null) {
            title = group

            val editor = sharedPreferences.edit()
            editor.putString(SPEC_NAME, spec)
            editor.putString(GROUP_NAME, group)
            editor.apply()

            // Ловит ошибку если был удалён файл сохранённой группы
            try {
                setViewPager("specs/${spec}/${group}.json")
            } catch (e: IOException) {
                if (spCleared())
                    finish()
            }
        } else {
            moveToGroupSelection()
        }
    }

    private fun setViewPager(path: String) {
        setContentView(R.layout.activity_main)

        val daysAdapter = DaysAdapter(parseJson(path), this)

        viewPager.adapter = daysAdapter
        viewPager.offscreenPageLimit = 3  // загружает по 3 страницы слева и справа от текущей
        viewPager.setCurrentItem(setPageOnToday(), false)

        attachTabs()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            var lastPosition: Int = 0

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                /*
                Сохраняет прошлую позицию и сравнивает с текущей. При попытке перемотки, если было
                6 и стало 6, перематывает на понедельник.
                 */
                if (lastPosition == 6 && position == 6)
                    viewPager.currentItem = 0
                lastPosition = position
            }
        })
    }

    private fun spCleared(): Boolean {
        /*
        Окрывает редактирование файла сохранений. Использует метод clear чтобы стереть всё и
        сохраняет изменения.
         */
        val editor = sharedPreferences.edit()
        editor.clear()
        return editor.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toGroup -> {
                moveToGroupSelection()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun moveToGroupSelection() {
        val intent = Intent(this, GroupSelectionActivity::class.java)
        startActivity(intent)
        finish()  // стираем чтобы не было возврата к прошлой выбранной группе
    }

    private fun attachTabs() {
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            Log.d("TAB_POSITION", position.toString())
            tab.text = when(position) {
                0 -> "пн"
                1 -> "вт"
                2 -> "ср"
                3 -> "чт"
                4 -> "пт"
                5 -> "сб"
                6 -> "вс"
                else -> ""
            }
        }.attach()
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
