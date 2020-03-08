package com.it_club.timetable_kuam.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it_club.timetable_kuam.R
import com.it_club.timetable_kuam.model.NotificationItem
import com.it_club.timetable_kuam.utils.inflate
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationsAdapter(private val notifications: List<NotificationItem>)
    : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        return NotificationViewHolder(parent.inflate(R.layout.item_notification))
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: NotificationItem) {
            itemView.notificationText.text = item.text
            // TODO: Add date and day of the week
        }
    }
}