package com.example.timetable_kuam.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.timetable_kuam.DayFragment

class DaysAdapter(private val jsonTable: String, fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount() = 7

    override fun createFragment(position: Int): Fragment {
        return DayFragment.newInstance(position, jsonTable)
    }
}