package com.it_club.timetable_kuam.utils

object TimeUtils {

    private val timePairs = listOf(
        Pair("08:00", "09:10"),
        Pair("09:20", "10:30"),
        Pair("10:40", "11:50"),
        Pair("12:10", "13:20"),
        Pair("13:30", "14:40"),
        Pair("14:50", "16:00")
    )

    fun getStartTime(id: Int) = timePairs[id].first

    fun getEndTime(id: Int) = timePairs[id].second
}