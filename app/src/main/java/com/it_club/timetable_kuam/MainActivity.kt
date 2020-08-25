package com.it_club.timetable_kuam

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.db.FirestoreService
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var chair: String? = null
    private var group: String? = null
    private var isBlinking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(USER_FILE, MODE)
        chair = sharedPreferences.getString(CHAIR_NAME, null)
        group = sharedPreferences.getString(GROUP_NAME, null)
        isBlinking = sharedPreferences.getBoolean(IS_BLINKING, isBlinking)

        if (chair == null && group == null) {
            moveToSelectionActivity()
        } else {
            getTimetable()
            title = group
        }

        setContentView(R.layout.activity_main)
    }

    private fun getTimetable() {
        FirestoreService.timetable(chair!!, group!!)
            .get()
            .addOnSuccessListener { result ->
                fillTimetable(result.toObjects(ClassItem::class.java))
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                chair = data?.getStringExtra(CHAIR_NAME)
                group = data?.getStringExtra(GROUP_NAME)
                isBlinking = data?.getBooleanExtra(IS_BLINKING, false)!!

                sharedPreferences.edit().apply {
                    putString(CHAIR_NAME, chair)
                    putString(GROUP_NAME, group)
                    putBoolean(IS_BLINKING, isBlinking)
                    apply()
                }

                val classItemArray = data.getParcelableArrayExtra(CLASS_ITEM_ARRAY)
                @Suppress("UNCHECKED_CAST")
                fillTimetable(classItemArray?.toList() as List<ClassItem>)
                title = group
            }
        }
    }

    private fun fillTimetable(timetable: List<ClassItem>) {
        viewPager.adapter = DaysAdapter(timetable, this)
        viewPager.offscreenPageLimit = 4
        viewPager.setCurrentItem(onToday(), false)

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

    private fun onToday(): Int {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
        return if (today < 5) today else 0
    }

    private fun moveToSelectionActivity() {
        startActivityForResult(Intent(this,
            SelectionActivity::class.java),
            SELECTION_REQUEST_CODE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.moveToGroupSelection -> {
                moveToSelectionActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}