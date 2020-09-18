package com.it_club.timetable_kuam.utils

import java.util.*

val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница")

private val timePairs = listOf(
    Pair("08:00", "08:50"),
    Pair("09:00", "09:50"),
    Pair("10:00", "10:50"),
    Pair("11:10", "12:00"),
    Pair("12:10", "13:00"),
    Pair("13:10", "14:00")
)

fun getStartTime(id: Int) = timePairs[id].first

fun getEndTime(id: Int) = timePairs[id].second

fun onToday(): Int {
    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
    return if (today < 5) today else 0
}
