package com.example.nodrama.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.nodrama.R
import com.example.nodrama.model.Entities.Leave
import com.example.nodrama.view.PastLeaveAdapter.PastLeaveViewHolder

class PastLeaveAdapter :
    ListAdapter<Leave, PastLeaveViewHolder>(LEAVES_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastLeaveViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.past_leave_item_view, parent, false)
        return PastLeaveViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastLeaveViewHolder, position: Int) {
        val current = getItem(position)
        val datePeriod = "${current.startDate} to ${current.endDate}"
        holder.bind(current.leaveType.toString(), datePeriod, current.status.toString(), current)
    }


    class PastLeaveViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val pastTypeView: TextView = itemView.findViewById(R.id.textViewPastType)
        private val pastDateView: TextView = itemView.findViewById(R.id.textViewPastDate)
        private val pastStatusView: TextView = itemView.findViewById(R.id.textViewPastStatus)
        private var leave: Leave = Leave()

        fun bind(leaveType: String, date: String, leaveStatus: String, leaveSelected: Leave) {
            pastTypeView.text = leaveType
            pastDateView.text = date
            pastStatusView.text = leaveStatus
            leave = leaveSelected
        }
    }

    companion object {
        private val LEAVES_COMPARATOR = object : DiffUtil.ItemCallback<Leave>() {
            override fun areItemsTheSame(oldItem: Leave, newItem: Leave): Boolean {
                return oldItem === newItem
            }
            override fun areContentsTheSame(oldItem: Leave, newItem: Leave): Boolean {
                return true
            }
        }
    }


}