package com.it_club.timetable_kuam.services

import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.it_club.timetable_kuam.model.NotificationItem
import com.it_club.timetable_kuam.model.NotificationsManager
import java.io.File

class TimetableFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message is received from ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val data = remoteMessage.data["test"]
        Log.d(TAG, "Notification data: $data")

        NotificationsManager.newMessages++
        Log.d(TAG, "New notifications: ${NotificationsManager.newMessages}")

        Log.d(TAG, "Notification title: ${title}")
        Log.d(TAG, "Notification body: ${body}")
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    companion object {
        const val TAG = "FirebaseMsgService"
    }
}