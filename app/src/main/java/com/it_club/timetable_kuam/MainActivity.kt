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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var sp: SharedPreferences
    private lateinit var registrationWeek1: ListenerRegistration
    private lateinit var registrationWeek2: ListenerRegistration
    private var chair: String? = null
    private var group: String? = null
    private var isBlinking: Boolean = false
    private var timetableWeek1 = listOf<ClassItem>()
    private var timetableWeek2 = listOf<ClassItem>()
    private var currentWeek: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Firebase.firestore
        sp = getSharedPreferences(USER_FILE, MODE)

        chair = sp.getString(CHAIR_NAME, chair)
        group = sp.getString(GROUP_NAME, group)
        isBlinking = sp.getBoolean(IS_BLINKING, isBlinking)

        currentWeek = savedInstanceState?.getInt(CURRENT_WEEK) ?: currentWeek

        if (chair == null && group == null) {
            moveToSelectionActivity()
        }

        setContentView(R.layout.activity_main)
    }

    private fun moveToSelectionActivity() {
        startActivityForResult(Intent(this,
            SelectionActivity::class.java),
            SELECTION_REQUEST_CODE)
    }

    override fun onStart() {
        super.onStart()
        getTimetable()
        title = group
    }

    private fun querySubCollection(): Query {
        return db.collection(chair!!)
            .document(group!!)
            .collection(FIRST_HALF)
    }

    private fun getTimetable() {
        registrationWeek1 = querySubCollection()
            .whereEqualTo("week_id", 0)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                Log.d(TAG, ">> timetable updated ${snapshots?.documentChanges}")
                timetableWeek1 = snapshots?.toObjects(ClassItem::class.java) ?: timetableWeek1
                fillTimetable(timetableWeek1)
            }

        if (isBlinking) {
            registrationWeek2 = querySubCollection()
                .whereEqualTo("week_id", 1)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    Log.d(TAG, ">> timetable updated")
                    timetableWeek2 = snapshots?.toObjects(ClassItem::class.java) ?: timetableWeek1
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                group = data?.getStringExtra(GROUP_NAME)
                chair = data?.getStringExtra(CHAIR_NAME)
                isBlinking = data?.getBooleanExtra(IS_BLINKING, false)!!

                sp.edit().apply {
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
        // Set smoothScroll to false to remove the setting animation when the app is opened
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (isBlinking) {
            val item1 = menu?.findItem(R.id.switchToWeek1)
            val item2 = menu?.findItem(R.id.switchToWeek2)

            item1?.isVisible = true
            item2?.isVisible = true

            if (currentWeek == 0) {
                item1?.isEnabled = false
            } else {
                item2?.isEnabled = false
            }
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
                if (currentWeek != 0) {
                    fillTimetable(timetableWeek1)
                    currentWeek = 0
                    invalidateOptionsMenu()
                }
                true
            }
            R.id.switchToWeek2 -> {
                if (currentWeek != 1) {
                    fillTimetable(timetableWeek2)
                    currentWeek = 1
                    invalidateOptionsMenu()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_WEEK, currentWeek)
    }

    override fun onStop() {
        super.onStop()
        // Detach DB real-time changes listener when the app is no longer active
        registrationWeek1.remove()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}