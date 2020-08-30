package com.it_club.timetable_kuam

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private var chair: String? = null
    private var group: String? = null
    private var isBlinking: Boolean = false
    private var timetableWeek1 = listOf<ClassItem>()
    private var timetableWeek2 = listOf<ClassItem>()
    private var currentWeek: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Firebase.firestore
        sharedPreferences = getSharedPreferences(USER_FILE, MODE)

        chair = sharedPreferences.getString(CHAIR_NAME, null)
        group = sharedPreferences.getString(GROUP_NAME, null)
        isBlinking = sharedPreferences.getBoolean(IS_BLINKING, false)

        isBlinking = true

        currentWeek = savedInstanceState?.getInt(CURRENT_WEEK) ?: currentWeek

        if (chair == null && group == null) {
            moveToSelectionActivity()
        } else {
            getTimetable()
            title = group
        }

        setContentView(R.layout.activity_main)
    }

    private fun getTimetable() {
        if (isBlinking) {
            val nextWeek = if (currentWeek == 0) 1 else 0
            getTimetableForWeek(currentWeek)
            getTimetableForWeek(nextWeek)
        } else {
            getTimetableForWeek(0)
        }
    }

    private fun getTimetableForWeek(weekId: Int) {
        db.collection(chair!!)
            .document(group!!)
            .collection(FIRST_HALF)
            .whereEqualTo("week_id", weekId)
            .get()
            .addOnSuccessListener { result ->
                if (weekId == 0) {
                    timetableWeek1 = result.toObjects(ClassItem::class.java)
                    fillTimetable(timetableWeek1)
                } else {
                    timetableWeek2 = result.toObjects(ClassItem::class.java)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                group = data?.getStringExtra(GROUP_NAME)
                chair = data?.getStringExtra(CHAIR_NAME)
                isBlinking = data?.getBooleanExtra(IS_BLINKING, false)!!

                sharedPreferences.edit().apply {
                    putString(CHAIR_NAME, chair)
                    putString(GROUP_NAME, group)
                    putBoolean(IS_BLINKING, isBlinking)
                    apply()
                }

                getTimetable()
                title = group
            }
        }
    }

    private fun fillTimetable(timetable: List<ClassItem>) {
        Log.d(TAG, ">>>> fill timetable")
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (isBlinking) {
            val item1 = menu?.findItem(R.id.switchToWeek1)
            val item2 = menu?.findItem(R.id.switchToWeek2)
            item1?.isVisible = true
            item2?.isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.moveToGroupSelection -> {
                moveToSelectionActivity()
                true
            }
            R.id.switchToWeek1 -> {
                fillTimetable(timetableWeek1)
                currentWeek = 0
                true
            }
            R.id.switchToWeek2 -> {
                fillTimetable(timetableWeek2)
                currentWeek = 1
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_WEEK, currentWeek)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}