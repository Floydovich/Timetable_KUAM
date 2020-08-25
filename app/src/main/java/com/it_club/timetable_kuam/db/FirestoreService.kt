package com.it_club.timetable_kuam.db

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.it_club.timetable_kuam.MainActivity
import com.it_club.timetable_kuam.model.ClassItem

object FirestoreService {
    val db = Firebase.firestore

    fun chair(chairId: String) = db.collection(chairId)

    fun group(chairId: String, groupId: String) = chair(chairId).document(groupId)

    fun timetable(chairId: String, groupId: String) = group(chairId, groupId)
        .collection("Расписание-1")
}