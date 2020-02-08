package com.example.timetable_kuam

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timetable_kuam.model.ClassItem
import com.example.timetable_kuam.utils.TimeUtils
import com.example.timetable_kuam.utils.inflate
import kotlinx.android.synthetic.main.item_class.view.*

class ClassesAdapter(
    private val classes: List<ClassItem>?
) : RecyclerView.Adapter<ClassesAdapter.ViewHolder>() {

    override fun getItemCount() = classes?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_class)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classItem = classes?.get(position)

        if (classItem != null) {
            holder.view.name.text = classItem.name
            holder.view.prof.text = classItem.prof
            holder.view.place.text = classItem.place

            holder.view.startTime.text = TimeUtils.getStartTime(classItem.id)
            holder.view.endTime.text = TimeUtils.getEndTime(classItem.id)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}