package com.it_club.timetable_kuam.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.it_club.timetable_kuam.DayFragment
import com.it_club.timetable_kuam.model.ClassItem

class DaysAdapter(timetable: List<ClassItem>, fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val classesGroupedByDay = timetable.groupBy { it.day }

    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
        val timetable = classesGroupedByDay[position]
        return if (timetable != null) DayFragment.newInstance(timetable) else Fragment()
    }
}