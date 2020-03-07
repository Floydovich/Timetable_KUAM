package com.it_club.timetable_kuam

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.model.NotificationsManager
import com.it_club.timetable_kuam.utils.GROUP_NAME
import com.it_club.timetable_kuam.utils.MODE
import com.it_club.timetable_kuam.utils.SPEC_NAME
import com.it_club.timetable_kuam.utils.USER_FILE
import com.nex3z.notificationbadge.NotificationBadge
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtil
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var filePath: String
    private lateinit var notificationBadge: NotificationBadge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(USER_FILE, MODE)

        val spec = intent.getStringExtra(SPEC_NAME) ?:
            sharedPreferences.getString(SPEC_NAME, null)
        
        val group = intent.getStringExtra(GROUP_NAME) ?:
            sharedPreferences.getString(GROUP_NAME, null)

        if (group != null && spec != null) {
            title = group
            filePath = "specs/${spec}/${group}.json"

            val editor = sharedPreferences.edit()
            editor.putString(SPEC_NAME, spec)
            editor.putString(GROUP_NAME, group)
            editor.apply()

            // Ловит ошибку если был удалён файл сохранённой группы
            try {
                val timetable = Gson().fromJson<List<ClassItem>>(
                    IOUtil.toString(assets.open(filePath)),
                    object : TypeToken<List<ClassItem>>() {}.type
                )

                viewPager.adapter = DaysAdapter(timetable, this)
                viewPager.offscreenPageLimit = 3  // загружает по 3 страницы слева и справа от текущей
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
            } catch (e: IOException) {
                if (spIsCleared())
                    finish()
            }
        } else {
            moveToGroupSelection()
        }
    }

    private fun spIsCleared(): Boolean {
        /*
        Окрывает редактирование файла сохранений. Использует метод clear чтобы стереть всё и
        сохраняет изменения.
         */
        val editor = sharedPreferences.edit()
        editor.clear()
        return editor.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val notifications = menu?.findItem(R.id.notifications)
        val actionView = notifications?.actionView
        notificationBadge = actionView?.findViewById(R.id.badge)!!

        actionView.setOnClickListener {
            NotificationsManager.newMessages = 0
            invalidateOptionsMenu()
            onOptionsItemSelected(notifications)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (NotificationsManager.newMessages > 0)
            notificationBadge.setNumber(NotificationsManager.newMessages)
        else
            notificationBadge.clear()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.moveToGroupSelection -> {
                moveToGroupSelection()
                true
            }
            R.id.notifications -> {
                moveToNotifications()
                true
            }
//            R.id.subscribeToTopic -> {
//                FirebaseMessaging.getInstance().subscribeToTopic("test2")
//                    .addOnCompleteListener { task ->
//                        var msg = "Subscribed to test2."
//                        if (!task.isSuccessful) {
//                            msg = "Failed to subscribe to test2."
//                        }
//                        Log.d("FirebaseMsgService", msg)
//                        Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
//                    }
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun moveToNotifications() {
        val intent = Intent(this, NotificationsActivity::class.java)
        startActivity(intent)
    }

    private fun moveToGroupSelection() {
        val intent = Intent(this, GroupSelectionActivity::class.java)
        startActivity(intent)
        finish()  // стираем чтобы не было возврата к прошлой выбранной группе
    }

    private fun setDay(): Int {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2  // т.к. по ум. Сб = 0
        return if (today < 5) today else 0
    }
}
