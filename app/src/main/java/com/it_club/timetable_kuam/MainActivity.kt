package com.it_club.timetable_kuam

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.helpers.*
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences(USER_FILE, MODE) }
    private val firebaseMessaging = FirebaseMessaging.getInstance()
    private val db = Firebase.firestore
    private var dbListener: ListenerRegistration? = null
    private var chair: String? = null
    private var group: String? = null
    private var isBlinking: Boolean = false
    private var topic: String? = null
    private var currentWeek: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chair = sharedPreferences.getString(CHAIR, chair)
        group = sharedPreferences.getString(GROUP, group)
        isBlinking = sharedPreferences.getBoolean(IS_BLINKING, isBlinking)
        topic = sharedPreferences.getString(TOPIC, topic)
        currentWeek = savedInstanceState?.getInt(CURRENT_WEEK) ?: currentWeek

        if (group == null)
            moveToSelectionActivity()

        setContentView(R.layout.activity_main)
        title = group
    }

    override fun onStart() {
        super.onStart()
        // Do not start listener if the app is opened for the first time on a new device
        if (group != null)
            startDbListener()
    }

    private fun moveToSelectionActivity() {
        startActivityForResult(Intent(this,
            SelectionActivity::class.java),
            SELECTION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Reset from 1 to 0 on switching from a blinking to a non-blinking group
            currentWeek = 0
            group = data?.getStringExtra(GROUP)
            chair = data?.getStringExtra(CHAIR)

            if (topic != null)
                unsubscribeFromMessages(firebaseMessaging, topic!!)

            topic = transliterateGroupToTopic(group!!)
            subscribeToMessages(firebaseMessaging, topic!!)

            // Only the "cc" groups could be blinking and some of them are not
            if ("сс" in group!!) {
                db.collection(chair!!)
                    .document(group!!)
                    .get()
                    .addOnSuccessListener { result ->
                        isBlinking = result["blinking"] as Boolean
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error on getting blinking field: $exception.")
                    }
            } else {
                isBlinking = false
            }

            saveGroupPreferences(sharedPreferences, chair!!, group!!, isBlinking, topic!!)
            startDbListener()
            invalidateOptionsMenu()
            title = group
        }
    }

    private fun startDbListener() {
        /*
        Call Firestore DB to get a timetable with a current week id. Also this method is called
        when the user changes the group or when they changes the week.
         */
        dbListener = db.timetableForTerm(chair!!, group!!)
            .whereEqualTo("week_id", currentWeek)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val timetable = snapshots.toObjects(ClassItem::class.java)
                    fillTimetable(timetable)
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
        val weekButton = menu?.findItem(R.id.switchWeeks)

        if (isBlinking) {
            weekButton?.isVisible = true
            weekButton?.title = "Неделя ${currentWeek + 1}"
        } else {
            weekButton?.isVisible = false
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
            R.id.switchWeeks -> {
                currentWeek = if (currentWeek == 0) 1 else 0
                startDbListener()
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

    override fun onStop() {
        super.onStop()
        dbListener?.remove()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}