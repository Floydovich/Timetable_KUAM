package com.it_club.timetable_kuam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Указывает что этот класс создан для передачи между активити и сохранения в Bundle
@Parcelize
data class ClassItem(
    val day_id: Int = 0,
    val class_id: Int = 0,
    val name: String = "",
    val kind: String = "",
    val prof: String = "",
    val place: String = ""
) : Parcelable
