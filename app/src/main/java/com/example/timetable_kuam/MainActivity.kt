package com.example.timetable_kuam

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.timetable_kuam.adapters.DaysAdapter
import com.example.timetable_kuam.model.ClassItem
import com.example.timetable_kuam.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtil
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        /*

         */
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(USER_FILE, MODE)

        val spec = intent.getStringExtra(SPEC_NAME) ?: 
            sharedPreferences.getString(SPEC_NAME, null)
        
        val group = intent.getStringExtra(GROUP_NAME) ?:
            sharedPreferences.getString(GROUP_NAME, null)

        if (group != null && spec != null) {
            title = group

            savePath(SPEC_NAME, spec)
            savePath(GROUP_NAME, group)

            setViewPager("specs/${spec}/${group}.json")
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
    }

    private fun savePath(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
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
