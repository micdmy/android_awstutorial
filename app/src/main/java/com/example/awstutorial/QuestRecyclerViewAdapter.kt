package com.example.awstutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestRecyclerViewAdapter(
        private val values: MutableList<UserData.Quest>?) :
        RecyclerView.Adapter<QuestRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.content_quest, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestRecyclerViewAdapter.ViewHolder, position: Int) {

            val quest = values?.get(position)
            holder.nameView.text = quest?.name ?: "?"
            holder.playersProgressView.text = quest?.playersProgress?.name ?: "?"
        }

        override fun getItemCount() = values?.size ?: 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameView: TextView = view.findViewById(R.id.name)
            val playersProgressView: TextView = view.findViewById(R.id.players_progress)
        }
}