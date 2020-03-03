package com.example.timetable_kuam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Указывает что этот класс создан для передачи между активити и сохранения в Bundle
@Parcelize
data class ClassItem(
    val day: Int,
    val id: Int,
    val name: List<String>,
    val prof: List<String>,
    val place: List<String>
) : Parcelable
