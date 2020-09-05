package com.it_club.timetable_kuam

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var sp: SharedPreferences
    private lateinit var registration: ListenerRegistration
    private var chair: String? = null
    private var group: String? = null
    private var isBlinking: Boolean = false
    private var currentWeek: Int = 0
    private var timetable = listOf<ClassItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = "Notifications for the timetable updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
                isBlinking = data?.getBooleanExtra(IS_BLINKING, false)!!

                sp.edit().apply {
                    putString(CHAIR_NAME, chair)
                    putString(GROUP_NAME, group)
                    putBoolean(IS_BLINKING, isBlinking)
                    apply()
                }

                getTimetable()
                title = group
                // Refresh the app bar to show I and II when the group is changed to a blinking one
                invalidateOptionsMenu()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Start the registration listener every time the user opens the app from the background.
        getTimetable()
        title = group
    }

    private fun querySubCollection(): Query {
        return db.collection(chair!!)
            .document(group!!)
            .collection(FIRST_HALF)
    }

    private fun getTimetable() {
        registration = querySubCollection()
            .whereEqualTo("week_id", currentWeek)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    timetable = snapshots.toObjects(ClassItem::class.java)

                    fillTimetable(timetable)

                    val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница")

                    for (classItem in snapshots.documentChanges) {
                        when (classItem.type) {
                            DocumentChange.Type.MODIFIED -> {
                                val day = days[classItem.newIndex / 6]
                                Log.d(TAG, "New index: ${classItem.newIndex}")
                                val classId = classItem.newIndex % 6 + 1
                                val name = classItem.document["name"]

                                val text = "$day, $classId пара - ${if (name == "") "Окно" else name}"

                                val notificationId = createID()
                                Log.d(TAG, "Notification ID: $notificationId")

                                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_baseline_update_24)
                                    .setContentTitle("Изменение в расписании")
                                    .setContentText(text)
                                    .setStyle(NotificationCompat.BigTextStyle()
                                        .bigText(text))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                                // notificationId is a unique int for each notification that you must define
                                NotificationManagerCompat.from(this)
                                    .notify(notificationId, builder.build())
                            }
                            else -> {}  // do nothing if the document didn't change
                        }
                    }
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

    override fun onStop() {
        super.onStop()
        // Detach DB real-time changes listener when the app is no longer active
        registration.remove()
    }

    companion object {
        const val TAG = "MainActivity"
        const val CHANNEL_ID = "timetable_kuam.notification_update"
    }
}