package com.example.nodrama.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.nodrama.R
import com.example.nodrama.model.Entities.Leave
import com.example.nodrama.view.PendingLeaveAdapter.PendingLeaveViewHolder

class PendingLeaveAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Leave, PendingLeaveViewHolder>(LEAVES_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingLeaveViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_leave_item_view, parent, false)
        return PendingLeaveViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: PendingLeaveViewHolder, position: Int) {
        val current = getItem(position)
        val datePeriod = "${current.startDate} to ${current.endDate}"
        holder.bind(current.leaveType.toString(), datePeriod, current.status.toString(), current)
    }

    class PendingLeaveViewHolder(itemView: View, private val onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val pendingTypeView: TextView = itemView.findViewById(R.id.textViewPendingType)
        private val pendingDateView: TextView = itemView.findViewById(R.id.textViewPendingDate)


        private val pendingStatusView: TextView = itemView.findViewById(R.id.textViewPendingStatus)
        private val deleteButton: ImageView = itemView.findViewById(R.id.buttonDeleteLeave)

        private var leave: Leave = Leave()

        fun bind(leaveType: String, date: String, leaveStatus: String, leaveSelected: Leave) {

            pendingTypeView.text = leaveType
            pendingDateView.text = date
            pendingStatusView.text = leaveStatus
            leave = leaveSelected
            deleteButton.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onClickListener.onItemClick(leave)
        }
    }

    interface OnClickListener {
        fun onItemClick(leave: Leave?)
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