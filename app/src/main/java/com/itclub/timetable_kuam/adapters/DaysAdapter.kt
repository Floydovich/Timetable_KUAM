package com.itclub.timetable_kuam.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.itclub.timetable_kuam.DayFragment
import com.itclub.timetable_kuam.model.ClassItem

class DaysAdapter(timetable: List<ClassItem>, fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val classesGroupedByDay = timetable.groupBy { it.day }

    override fun getItemCount() = 7

    override fun createFragment(position: Int): Fragment {
        val timetable = classesGroupedByDay[position]

        return if (timetable != null)
            DayFragment.newInstance(timetable)
        else
            // TODO: Make fragment for weekend that displays something
            Fragment()
    }
}