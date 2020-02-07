package com.example.timetable_kuam

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.timetable_kuam.model.Class_

class DaysAdapter(val classes: ArrayList<Class_>, fa: FragmentActivity) : FragmentStateAdapter(fa) {

    //  Группирует классы по дням и помещает в Map, в котором для ключа берётся Class_.day,
    //  а значением будет List всех классов, у которых .day равен ключу, например
    //  Понедельник => {0=[Class_, Class_, Class_, Class_],
    //  Вторник     =>  1=[Class_, Class_, Class_, Class_]}
    val classesGroupedByDay =  classes.groupBy { it.day }

    override fun getItemCount(): Int {
        return 7
    }

    // Создаёт фрагмент дня при листании
    override fun createFragment(position: Int): Fragment {
        return DayFragment(classesGroupedByDay[position])
    }
}