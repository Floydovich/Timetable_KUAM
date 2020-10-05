package com.it_club.timetable_kuam.helpers

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.it_club.timetable_kuam.MainActivity


fun subscribeToMessages(fm: FirebaseMessaging, topic: String) {
    fm.subscribeToTopic(topic)
        .addOnCompleteListener { task ->
            var msg = "Subscribed to a topic: $topic"
            if (!task.isSuccessful) {
                msg = "Subscribe failed"
            }
            Log.d(MainActivity.TAG, msg)
        }
}

fun unsubscribeFromMessages(fm: FirebaseMessaging, topic: String) {
    fm.unsubscribeFromTopic(topic)
        .addOnCompleteListener { task ->
            var msg = "Unsubscribed from a topic: $topic"
            if (!task.isSuccessful) {
                msg = "Unsubscribe has failed"
            }
            Log.d(MainActivity.TAG, msg)
        }
}