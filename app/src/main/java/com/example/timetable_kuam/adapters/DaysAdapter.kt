package com.example.timetable_kuam.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.timetable_kuam.DayFragment
import com.example.timetable_kuam.model.ClassItem

class DaysAdapter(classes: List<ClassItem>, fa: FragmentActivity) : FragmentStateAdapter(fa) {

    //  Группирует классы по дням и помещает в Map, например
    //  {0=[ClassItem, ClassItem, ClassItem, ClassItem], 1=[...], ...}
    private val classesGroupedByDay =  classes.groupBy { it.day }

    override fun getItemCount() = 7

    override fun createFragment(position: Int): Fragment {
        return DayFragment(classesGroupedByDay[position])
    }
}