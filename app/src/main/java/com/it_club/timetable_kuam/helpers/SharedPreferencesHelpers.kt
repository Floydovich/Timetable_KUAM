package com.it_club.timetable_kuam.helpers

import android.content.SharedPreferences
import com.it_club.timetable_kuam.utils.CHAIR_NAME
import com.it_club.timetable_kuam.utils.GROUP_NAME
import com.it_club.timetable_kuam.utils.IS_BLINKING

fun saveGroupPreferences(sp: SharedPreferences, chair: String, group: String, isBlinking: Boolean) {
    // Schedule the save to sharedPrefs when the blinking fieild is got to
    // avoid saving false for blinking
    sp.edit().apply {
        putString(CHAIR_NAME, chair)
        putString(GROUP_NAME, group)
        putBoolean(IS_BLINKING, isBlinking)
        apply()
    }
}