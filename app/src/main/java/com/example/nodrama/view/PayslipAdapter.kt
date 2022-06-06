package com.example.nodrama.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nodrama.R
import com.example.nodrama.model.Entities.Payslip
import com.example.nodrama.view.PayslipAdapter.PayslipViewHolder


class PayslipAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Payslip, PayslipViewHolder>(PAYSLIP_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayslipViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.payslip_item_view, parent, false)
        return PayslipViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: PayslipViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.period.toString(), current.description.toString(), current.date.toString(), current)
    }

    class PayslipViewHolder(itemView: View, private val onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val textViewMonth: TextView = itemView.findViewById(R.id.textViewMonth)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val btnDownload: ImageView = itemView.findViewById(R.id.buttonDownloadPayslip)

        private var payslip: Payslip = Payslip()

        fun bind(period: String, description: String, date: String, payslipSelected: Payslip) {
            textViewMonth.text = period
            textViewDescription.text = description
            textViewDate.text = date
            payslip = payslipSelected
            itemView.setOnClickListener(this)
            btnDownload.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (view is ImageView){
                Log.d("PayslipAdapter", "on Download Clicked!!")
                onClickListener.onDownloadClick(payslip)
            } else {
                Log.d("PayslipAdapter", "on Item Clicked!!")
                onClickListener.onItemClick(payslip)
            }
        }
    }

    interface OnClickListener {
        fun onItemClick(payslip: Payslip?)
        fun onDownloadClick(payslip: Payslip?)
    }

    companion object {
        private val PAYSLIP_COMPARATOR = object : DiffUtil.ItemCallback<Payslip>() {
            override fun areItemsTheSame(oldItem: Payslip, newItem: Payslip): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Payslip, newItem: Payslip): Boolean {
                return true
            }
        }
    }


}