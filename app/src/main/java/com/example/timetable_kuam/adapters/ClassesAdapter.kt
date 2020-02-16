package com.example.timetable_kuam.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timetable_kuam.R
import com.example.timetable_kuam.model.ClassItem
import com.example.timetable_kuam.utils.TimeUtils
import com.example.timetable_kuam.utils.inflate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_class.view.*

class ClassesAdapter(val day: Int?, private val jsonTable: String?)
    : RecyclerView.Adapter<ClassesAdapter.ViewHolder>() {

    private val timetable = Gson().fromJson<List<ClassItem>>(
        jsonTable,
        object : TypeToken<List<ClassItem>>() {} .type
    )
    private var dayClassesSet = timetable.filter { it.day == day }

    override fun getItemCount() = dayClassesSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_class)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classItem = dayClassesSet[position]

        holder.view.name.text = classItem.name
        holder.view.prof.text = classItem.prof
        holder.view.place.text = classItem.place

        holder.view.startTime.text = TimeUtils.getStartTime(classItem.id)
        holder.view.endTime.text = TimeUtils.getEndTime(classItem.id)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}