package com.it_club.timetable_kuam.helpers

import android.content.SharedPreferences
import com.it_club.timetable_kuam.utils.CHAIR
import com.it_club.timetable_kuam.utils.GROUP
import com.it_club.timetable_kuam.utils.IS_BLINKING
import com.it_club.timetable_kuam.utils.TOPIC

fun saveGroupPreferences(sp: SharedPreferences, chair: String, group: String, isBlinking: Boolean, topic: String) {
    // Schedule the save to sharedPrefs when the blinking fieild is got to
    // avoid saving false for blinking
    sp.edit().apply {
        putString(CHAIR, chair)
        putString(GROUP, group)
        putString(TOPIC, topic)
        putBoolean(IS_BLINKING, isBlinking)
        apply()
    }
}