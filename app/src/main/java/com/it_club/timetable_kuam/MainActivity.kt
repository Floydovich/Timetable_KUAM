package com.it_club.timetable_kuam

import android.app.Activity
import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.helpers.createNotificationChannel
import com.it_club.timetable_kuam.helpers.notifyChanges
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var sp: SharedPreferences
    private var chair: String? = null
    private var group: String? = null
    private var isBlinking: Boolean = false
    private var currentWeek: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                group = data?.getStringExtra(GROUP_NAME)
                chair = data?.getStringExtra(CHAIR_NAME)

                // Set the week to 0 due to a bug of switching a blinking group with II enabled
                // to a non-blinking group
                currentWeek = 0

                // Check if the group is blinking immediately after coming from Selection
                // activity
                db.collection(chair!!)
                    .document(group!!)
                    .get()
                    .addOnSuccessListener { result ->
                        isBlinking = result["blinking"] as Boolean

                        // Schedule the save to sharedPrefs when the blinking fieild is got to
                        // avoid saving false for blinking
                        sp.edit().apply {
                            putString(CHAIR_NAME, chair)
                            putString(GROUP_NAME, group)
                            putBoolean(IS_BLINKING, isBlinking)
                            apply()
                        }

                        // Refresh the app bar to show I and II when the group is changed to a blinking one
                        invalidateOptionsMenu()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error on getting blinking field: $exception.")
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Start the registration listener every time the user opens the app from the background.
        if (chair != null && group != null) {
            getTimetable()
            title = group
        }
    }

    private fun getTimetable() {
        /*
        Call Firestore DB to get a timetable with a current week id. Also this method is called
        when the user changes the group or when they changes the week.
         */
        db.timetableForTerm(chair!!, group!!)
            .whereEqualTo("week_id", currentWeek)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    fillTimetable(snapshots.toObjects(ClassItem::class.java))

                    // Check the documentChanges list and send notifications for a changed document
                    notifyChanges(this,
                        "Изменение в расписании",
                        snapshots.documentChanges)
                }
            }
    }

    private fun fillTimetable(timetable: List<ClassItem>) {
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
                currentWeek = 0
                getTimetable()
                invalidateOptionsMenu()
                true
            }
            R.id.switchToWeek2 -> {
                currentWeek = 1
                getTimetable()
                invalidateOptionsMenu()
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