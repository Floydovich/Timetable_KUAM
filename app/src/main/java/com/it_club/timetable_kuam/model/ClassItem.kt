package com.it_club.timetable_kuam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Указывает что этот класс создан для передачи между активити и сохранения в Bundle
@Parcelize
data class ClassItem(
    val day: Int = 0,
    val id: Int = 0,
    val name: String = "",
    val place: String = "",
    val prof: String = ""
) : Parcelable
