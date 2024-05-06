package com.example.irontracker.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.irontracker.databinding.FragmentScoreboardBinding
import com.example.irontracker.databinding.ItemScoreboardBinding
import com.example.irontracker.ui.data.ScoreboardItem

class ScoreboardFragment : Fragment() {

    private var _binding: FragmentScoreboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scoreboardViewModel = ViewModelProvider(this).get(ScoreboardViewModel::class.java)

        _binding = FragmentScoreboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.scoreboardList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe the scoreboard data from the ViewModel
        scoreboardViewModel.scoreboardData.observe(viewLifecycleOwner) { scoreboardItems ->
            val adapter = ScoreboardAdapter(scoreboardItems)
            recyclerView.adapter = adapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ScoreboardAdapter(private val scoreboardItems: List<ScoreboardItem>) :
        RecyclerView.Adapter<ScoreboardAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemScoreboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val scoreboardItem = scoreboardItems[position]
            holder.bind(scoreboardItem)
        }

        override fun getItemCount() = scoreboardItems.size

        inner class ViewHolder(private val binding: ItemScoreboardBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(scoreboardItem: ScoreboardItem) {
                binding.userName.text = scoreboardItem.userName
                binding.userScore.text = scoreboardItem.highestRatio.toString()
            }
        }
    }
}

data class ScoreboardItem(val userName: String, val userScore: String)