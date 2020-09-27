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
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.helpers.*
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences(USER_FILE, MODE) }
    private val db = Firebase.firestore
    private var dbListener: ListenerRegistration? = null
    private var chair: String? = null
    private var group: String? = null
    private var isBlinking: Boolean = false
    private var currentWeek: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chair = sharedPreferences.getString(CHAIR_NAME, chair)
        group = sharedPreferences.getString(GROUP_NAME, group)
        isBlinking = sharedPreferences.getBoolean(IS_BLINKING, isBlinking)
        currentWeek = savedInstanceState?.getInt(CURRENT_WEEK) ?: currentWeek

        if (chair == null && group == null)
            moveToSelectionActivity()

        setContentView(R.layout.activity_main)
        title = group
    }

    private fun moveToSelectionActivity() {
        startActivityForResult(Intent(this,
            SelectionActivity::class.java),
            SELECTION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            group = data?.getStringExtra(GROUP_NAME)
            chair = data?.getStringExtra(CHAIR_NAME)

            // Reset from 2 on switching from a blinking to a non-blinking group
            currentWeek = 0

            // Check if the group is blinking after coming from Selection activity
            db.collection(chair!!)
                .document(group!!)
                .get()
                .addOnSuccessListener { result ->
                    // Check is blinking only for "cc" groups
                    if ("сс" in group!!)
                        isBlinking = result["blinking"] as Boolean
                    saveGroupPreferences(sharedPreferences, chair!!, group!!, isBlinking)
                    // Refresh the app bar to show I and II when the group is changed to a blinking one
                    invalidateOptionsMenu()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error on getting blinking field: $exception.")
                }

            startDbListener()
            title = group
        }
    }

    override fun onStart() {
        super.onStart()
        // Do not start listener if the app is opened for the first time on a new device
        if (chair != null && group != null)
            startDbListener()
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
        if (isBlinking) {
            val item = menu?.findItem(R.id.switchWeeks)

            if (item != null) {
                item.isVisible = true

                if (currentWeek == 0)
                    item.title = "Неделя 1"
                else
                    item.title = "Неделя 2"
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