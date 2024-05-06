package com.example.irontracker.ui.scoreboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.irontracker.R

class ScoreboardAdapter(private val scoreboardList: List<ScoreboardItem>) :
    RecyclerView.Adapter<ScoreboardAdapter.ScoreboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreboardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scoreboard, parent, false)
        return ScoreboardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScoreboardViewHolder, position: Int) {
        val currentItem = scoreboardList[position]
        holder.userNameTextView.text = currentItem.userName
        holder.userScoreTextView.text = currentItem.userScore.toString()
        holder.userRankTextView.text = (position + 1).toString() // Set the ranking number
    }

    override fun getItemCount(): Int {
        return scoreboardList.size
    }

    class ScoreboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.user_name)
        val userScoreTextView: TextView = itemView.findViewById(R.id.user_score)
        val userRankTextView: TextView = itemView.findViewById(R.id.user_rank)
    }
}