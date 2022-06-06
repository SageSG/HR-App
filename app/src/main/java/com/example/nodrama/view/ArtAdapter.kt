package com.example.nodrama.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nodrama.R
import com.example.nodrama.model.Entities.Test

class ArtAdapter(private val resultList : List<Test>) : RecyclerView.Adapter<ArtAdapter.MyViewHolder>() {

    /**
     * Create View
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // add as View at the end?
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_artresultitem,parent,false)
        return MyViewHolder(itemView)
    }

    /**
     * Bind data
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = resultList[position]

        holder.result.text = currentitem.result
        holder.time.text = currentitem.time
    }

    /**
     * Retrieve number of ART results in the list
     */
    override fun getItemCount(): Int {
        return resultList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val result : TextView = itemView.findViewById(R.id.artResultTextView)
        val time : TextView = itemView.findViewById(R.id.timestampTextView)
    }
}