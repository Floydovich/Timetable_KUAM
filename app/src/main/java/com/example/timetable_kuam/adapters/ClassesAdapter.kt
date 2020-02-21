package com.example.timetable_kuam.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timetable_kuam.R
import com.example.timetable_kuam.model.ClassItem
import com.example.timetable_kuam.utils.TimeUtils
import com.example.timetable_kuam.utils.inflate
import kotlinx.android.synthetic.main.item_class.view.*

class ClassesAdapter(private val classes: Array<ClassItem>)
    : RecyclerView.Adapter<ClassesAdapter.ViewHolder>() {

    override fun getItemCount() = 6  // захардкодил потому что 6 пар максимум

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_class))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classItem = classes[position]

        holder.view.startTime.text = TimeUtils.getStartTime(classItem.id)
        holder.view.endTime.text = TimeUtils.getEndTime(classItem.id)

        holder.view.name.text = classItem.name
        holder.view.prof.text = classItem.prof
        holder.view.place.text = classItem.place

        // TODO: Assign position to number text view and test
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}