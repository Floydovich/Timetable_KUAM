package com.it_club.timetable_kuam.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.firestore.DocumentChange
import com.it_club.timetable_kuam.R
import com.it_club.timetable_kuam.utils.CHANNEL_ID
import com.it_club.timetable_kuam.utils.createID
import com.it_club.timetable_kuam.utils.days

fun createNotificationChannel(notificationManager: NotificationManager) {
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
        notificationManager.createNotificationChannel(channel)
    }
}

fun notifyChanges(context: Context, title: String, snapshots: List<DocumentChange>) {
    for (classItem in snapshots) {
        when (classItem.type) {
            DocumentChange.Type.MODIFIED -> {
                val day = days[classItem.newIndex / 6]
                val classId = classItem.newIndex % 6 + 1
                val name = classItem.document["name"]
                val text = "$day, $classId пара - ${if (name == "") "Окно" else name}"

                val builder =
                    buildNotification(context, title, text)

                // notificationId is a unique int for each notification that you must define
                NotificationManagerCompat.from(context)
                    .notify(createID(), builder.build())
            }
            else -> {}  // do nothing if the document didn't change
        }
    }
}

fun buildNotification(context: Context, title: String, text: String): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_update_24)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
}

