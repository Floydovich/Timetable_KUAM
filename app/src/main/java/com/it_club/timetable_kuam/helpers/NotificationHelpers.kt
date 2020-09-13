package com.it_club.timetable_kuam.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.it_club.timetable_kuam.R
import com.it_club.timetable_kuam.utils.CHANNEL_DESCRIPTION
import com.it_club.timetable_kuam.utils.CHANNEL_ID
import com.it_club.timetable_kuam.utils.CHANNEL_NAME
import java.text.SimpleDateFormat
import java.util.*

fun createID() = SimpleDateFormat("ddHHmmss", Locale.US).format(Date()).toInt()

fun createNotificationChannel(notificationManager: NotificationManager) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            .apply { description = CHANNEL_DESCRIPTION }

        // Register the channel within the system
        notificationManager.createNotificationChannel(channel)
    }
}

fun createNotificationText(day: String, classId: Int, name: String, place: String): String {
    // Put "Окно" instead of empty class and remove class room
    val newName = if (name == "") "Окно" else name
    val newPlace = if (newName == "Окно") "" else ", $place"
    return "$day, $classId пара - $newName$newPlace"  // remove trailing comma for "Окно"
}

fun buildNotification(context: Context, title: String, text: String): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_update_24)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
}