package com.it_club.timetable_kuam

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtil
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var spec: String? = "is"
    private var group: String? = "32"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore

        db.collection(spec + group)
            .get()
            .addOnSuccessListener { result ->
                val timetable: List<ClassItem> = result.toObjects(ClassItem::class.java)
                timetable.forEach { Log.d(TAG, it.name) }
                fillTimetable(timetable)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        title = group  // устанавливает заголовок в верхней панели
    }

    private fun fillTimetable(timetable: List<ClassItem>) {
        viewPager.adapter = DaysAdapter(timetable, this)
        viewPager.offscreenPageLimit = 4  // загружает по 3 страницы слева и справа от текущей
        viewPager.setCurrentItem(setDay(), false)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "пн"
                1 -> "вт"
                2 -> "ср"
                3 -> "чт"
                4 -> "пт"
                else -> ""
            }
        }.attach()
    }

    private fun parseJson(filePath: String): List<ClassItem> {
        return Gson().fromJson(
            IOUtil.toString(assets.open(filePath)),
            object : TypeToken<List<ClassItem>>() {}.type
        )
    }

    private fun setDay(): Int {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2  // т.к. по ум. Сб = 0
        return if (today < 5) today else 0
    }

    private fun moveToGroupSelection() {
        val intent = Intent(this, GroupSelectionActivity::class.java)
        startActivityForResult(intent, GROUP_SELECTION_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

//        val notifications = menu?.findItem(R.id.notifications)
//        val actionView = notifications?.actionView
//
//        actionView.setOnClickListener {
//            notificationBadge.clear()
//            onOptionsItemSelected(notifications)
//        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.moveToGroupSelection -> {
                moveToGroupSelection()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}