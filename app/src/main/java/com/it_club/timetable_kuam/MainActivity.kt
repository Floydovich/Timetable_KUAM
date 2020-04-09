package com.it_club.timetable_kuam

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.it_club.timetable_kuam.adapters.DaysAdapter
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.model.NotificationItem
import com.it_club.timetable_kuam.model.NotificationsManager
import com.it_club.timetable_kuam.utils.*
import com.nex3z.notificationbadge.NotificationBadge
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtil
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var spec: String? = null
    private var group: String? = null
    private lateinit var notificationBadge: NotificationBadge
    private lateinit var notificationReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras
        if (extras?.get("text") != null) {
            val intent = Intent(this, NotificationsActivity::class.java)
            intent.putExtra("FCM_MSG", extras)
            startActivity(intent)
        }

        // For Debug purposes
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("fcm", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token
                Log.d("fcm", token.toString())
            })

        // TODO: Test topic subscription again
//        FirebaseMessaging.getInstance().subscribeToTopic("test").addOnCompleteListener {
//            Log.d("fcm", "Success.")
//        }

        sharedPreferences = getSharedPreferences(USER_FILE, MODE)

        spec = sharedPreferences.getString(SPEC_NAME, null)
        group = sharedPreferences.getString(GROUP_NAME, null)

        val notifications = sharedPreferences.getString("NOTIFICATIONS", null)
        Log.d("fcm", ">>notifications in main= ${notifications.toString()}")

        if (notifications != null) {
            val tokenType = object : TypeToken<MutableList<NotificationItem>>(){}.type
            val parsedNotifications = Gson()
                .fromJson<MutableList<NotificationItem>>(notifications, tokenType)
            NotificationsManager.notifications = parsedNotifications
        }

        if (spec != null && group != null)
            setContent()
        else
            moveToGroupSelection()
    }

    override fun onStart() {
        super.onStart()
        /*
        Создаём анонимный объект BroadcastReceiver, который будет принимать уведомления от
        LocalBroadcastManager. Перезаписываем метод onReceive, чтобы при получении он увеличивал
        счётчик сообщений на 1 и обновлял число на значке колокольчика.
         */
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                NotificationsManager.counter++
                notificationBadge.setNumber(NotificationsManager.counter)
            }
        }

        /*
        Для этой активити, вызываем экземпляр LocalBroadcastManager, и регистрируем в нём
        notificationReceiver и IntentFilter с тегом сообщений которые мы хотим получать
         */
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(notificationReceiver, IntentFilter("New notification"))
    }

    override fun onStop() {
        super.onStop()
        // Убираем notificationReceiver из LocalBroadcastManager когда активити останавливается
        // TODO: See what will be if I don't unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver)
    }

    private fun setContent() {
        title = group  // устанавливает заголовок в верхней панели

        viewPager.adapter = DaysAdapter(parseJson("specs/${spec}/${group}.json"), this)
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
    }

    private fun parseJson(filePath: String): List<ClassItem> {
        return Gson().fromJson(
            IOUtil.toString(assets.open(filePath)),
            object : TypeToken<List<ClassItem>>() {}.type
        )
    }

    private fun setDay(): Int {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2  // т.к. по ум. Сб = 0
        return if (today < 5) today else 0
    }

    private fun moveToGroupSelection() {
        val intent = Intent(this, GroupSelectionActivity::class.java)
        startActivityForResult(intent, GROUP_SELECTION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GROUP_SELECTION_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    spec = data?.getStringExtra(SPEC_NAME)
                    group = data?.getStringExtra(GROUP_NAME)

                    val editor = sharedPreferences.edit()
                    editor.putString(SPEC_NAME, spec)
                    editor.putString(GROUP_NAME, group)
                    editor.apply()

                    setContent()
                }
                Activity.RESULT_CANCELED -> {
                    /*
                    Если пользователь перешёл по кнопке назад, но до этого не было сохранённой
                    группы, то приложение завершает работу. Сделано для кнопки "Сменить группу",
                    в случае, если передумал менять группу.
                     */
                    if (spec == null && group == null) {
                        finish()
                    }
                }
                else -> {
                    Log.d("logd", "Else branch in OnActivityResult has happened.")
                }
            }
        }
    }

    private fun moveToNotifications() {
        val intent = Intent(this, NotificationsActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val notifications = menu?.findItem(R.id.notifications)
        val actionView = notifications?.actionView
        notificationBadge = actionView?.findViewById(R.id.badge)!!

        actionView.setOnClickListener {
            notificationBadge.clear()
            onOptionsItemSelected(notifications)
        }
        return true
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}