package com.it_club.timetable_kuam

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var chair: String? = null
    private var group: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: Start activity for result before layout is loaded
        startActivityForResult(Intent(this, GroupSelectionActivity::class.java), GROUP_SELECTION_REQUEST_CODE)

        title = group

//        Firebase.firestore.collection(chair.toString())
//            .document(group.toString())
//            .collection("Расписание-1")
//            .get()
//            .addOnSuccessListener { result ->
//                fillTimetable(result.toObjects(ClassItem::class.java))
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GROUP_SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                chair = data?.getStringExtra(CHAIR_NAME)
                group = data?.getStringExtra(GROUP_NAME)
                Log.d(TAG, "Got chair and group: $chair $group")
            }
        }
    }

    private fun fillTimetable(timetable: List<ClassItem>) {
        viewPager.adapter = DaysAdapter(timetable, this)
        viewPager.offscreenPageLimit = 4
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

    private fun setDay(): Int {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
        return if (today < 5) today else 0
    }

    private fun moveToGroupSelection() {
        val intent = Intent(this, GroupSelectionActivity::class.java)
        startActivityForResult(intent, GROUP_SELECTION_REQUEST_CODE)
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