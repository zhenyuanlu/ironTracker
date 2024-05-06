package com.example.irontracker.ui.scoreboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.irontracker.ui.data.FirebaseRepository
import com.example.irontracker.ui.data.ScoreboardItem
import com.google.firebase.firestore.ListenerRegistration

class ScoreboardViewModel : ViewModel() {
    private val _scoreboardData = MutableLiveData<List<ScoreboardItem>>()
    val scoreboardData: LiveData<List<ScoreboardItem>> = _scoreboardData

    private val firebaseRepository = FirebaseRepository()
    private lateinit var scoreboardListener: ListenerRegistration

    init {
        setupScoreboardListener()
    }

    private fun setupScoreboardListener() {
        scoreboardListener = firebaseRepository.getScoreboardDataListener { scoreboardItems ->
            _scoreboardData.value = scoreboardItems
        }
    }

    override fun onCleared() {
        super.onCleared()
        scoreboardListener.remove()
    }
}