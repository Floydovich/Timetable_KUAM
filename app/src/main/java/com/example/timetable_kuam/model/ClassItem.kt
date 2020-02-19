package com.example.timetable_kuam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClassItem(
    val day: Int,
    val id: Int,
    val name: String,
    val prof: String,
    val place: String
) : Parcelable
