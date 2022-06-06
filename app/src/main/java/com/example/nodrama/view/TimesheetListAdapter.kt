package com.example.nodrama.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.nodrama.R
import com.example.nodrama.model.Entities.Timesheet
import com.example.nodrama.view.TimesheetListAdapter.TimesheetViewHolder

class TimesheetListAdapter : ListAdapter<Timesheet, TimesheetViewHolder>(TIMESHEET_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesheetViewHolder {
        return TimesheetViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TimesheetViewHolder, position: Int) {
        val current = getItem(position)
        val date = current.clockIn?.replaceAfter(" ", "")
        val clockInTime = current.clockIn?.replaceBefore(" ", "")
        val clockOutTime = current.clockOut?.replaceBefore(" ", "")
        holder.bind(date, clockInTime.toString(), clockOutTime.toString())
    }

    class TimesheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateItemView: TextView = itemView.findViewById(R.id.textViewDate)
        private val clockInItemView: TextView = itemView.findViewById(R.id.textViewClockInTime)
        private val clockOutItemView: TextView = itemView.findViewById(R.id.textViewClockOutTime)

        fun bind(date: String?, clockIn: String, clockOut: String) {
            dateItemView.text = date
            clockInItemView.text = clockIn
            clockOutItemView.text = clockOut
        }

        companion object {
            fun create(parent: ViewGroup): TimesheetViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.past_timesheet_item_view, parent, false)
                return TimesheetViewHolder(view)
            }
        }
    }

    companion object {
        private val TIMESHEET_COMPARATOR = object : DiffUtil.ItemCallback<Timesheet>() {
            override fun areItemsTheSame(oldItem: Timesheet, newItem: Timesheet): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Timesheet, newItem: Timesheet): Boolean {
                return true
            }
        }
    }

}