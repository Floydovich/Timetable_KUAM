package com.it_club.timetable_kuam.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

fun ViewGroup.inflate(layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

// Extension functions for Firestore to get collections and documents
// https://proandroiddev.com/firebase-android-series-firestore-17e8951c574e
fun FirebaseFirestore.timetableForTerm(
    chair: String,
    group: String,
    term: String = FIRST_HALF
): CollectionReference {
    return collection("$chair/$group/$term")
}