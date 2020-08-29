package com.it_club.timetable_kuam

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Firebase.firestore

        sharedPreferences = getSharedPreferences(USER_FILE, MODE)
        val chair = sharedPreferences.getString(CHAIR_NAME, null)
        val group = sharedPreferences.getString(GROUP_NAME, null)
        val isBlinking = sharedPreferences.getBoolean(IS_BLINKING, false)

        if (chair != null && group != null) {
            getTimetable(chair, group, isBlinking)
            title = group
        } else {
            moveToSelectionActivity()
        }

        setContentView(R.layout.activity_main)
    }

    private fun getTimetable(chair: String, group: String, isBlinking: Boolean) {
        val weekId = if (isBlinking) 1 else 0
        db.collection(chair)
            .document(group)
            .collection(FIRST_HALF)
            .whereEqualTo("week_id", weekId)
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

        Log.d(TAG, ">>>> On activity result called")

        if (requestCode == SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val group = data?.getStringExtra(GROUP_NAME)
                val chair = data?.getStringExtra(CHAIR_NAME)
                val isBlinking = data?.getBooleanExtra(IS_BLINKING, false)!!

                sharedPreferences.edit().apply {
                    putString(CHAIR_NAME, chair)
                    putString(GROUP_NAME, group)
                    putBoolean(IS_BLINKING, isBlinking)
                    apply()
                }

                getTimetable(chair!!, group!!, isBlinking)
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