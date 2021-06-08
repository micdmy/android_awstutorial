package com.example.awstutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemRecyclerViewAdapter(
        private val values: MutableList<UserData.Item>?) :
        RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.content_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemRecyclerViewAdapter.ViewHolder, position: Int) {

            val item = values?.get(position)
            holder.nameView.text = item?.name
            holder.descriptionView.text = item?.description
            holder.locationView.text = item?.location
        }

        override fun getItemCount() = values?.size ?: 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameView: TextView = view.findViewById(R.id.name)
            val descriptionView: TextView = view.findViewById(R.id.story)
            val locationView: TextView = view.findViewById(R.id.remarks)
        }
}