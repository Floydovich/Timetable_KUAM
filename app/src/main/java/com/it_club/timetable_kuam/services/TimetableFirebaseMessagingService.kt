package com.it_club.timetable_kuam.services

import android.content.BroadcastReceiver
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.it_club.timetable_kuam.MainActivity
import com.it_club.timetable_kuam.model.NotificationItem
import com.it_club.timetable_kuam.model.NotificationsManager

class TimetableFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var broadcaster: LocalBroadcastManager

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
        Log.d("fcm", "The broadcasrter is: ${broadcaster.toString()}")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("fcm", "Message received.")
        Log.d("fcm", remoteMessage.data["body"].toString())

        val notification = remoteMessage.notification
        // Add message to all notifications
        if (notification != null)
            NotificationsManager.notifications.add(NotificationItem(notification.title, notification.body))
        Log.d("fcm", "Added notification from remote message: ${NotificationsManager.notifications}")

        val intent = Intent("New notification")
        broadcaster.sendBroadcast(intent)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    companion object {
        const val TAG = "fcm"
    }
}