// FirebaseRepository.kt
package com.example.irontracker.ui.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    fun getScoreboardDataListener(callback: (List<ScoreboardItem>) -> Unit): ListenerRegistration {
        return FirebaseFirestore.getInstance().collection("scoreboard")
            .orderBy("highestRatio", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                val scoreboardItems = querySnapshot?.documents?.mapNotNull { document ->
                    document.toObject(ScoreboardItem::class.java)
                } ?: emptyList()

                callback(scoreboardItems)
            }
    }
    fun uploadTaskItem(taskItem: TaskItem) {
        val taskData = hashMapOf(
            "taskName" to taskItem.taskName,
            "taskWeight" to taskItem.taskWeight,
            "currentWeight" to taskItem.currentWeight,
            "order" to taskItem.order,
            "userId" to taskItem.userId
        )
        db.collection("tasks").add(taskData)
    }

    fun getTopScoreboardItems(resultListener: (List<ScoreboardItem>) -> Unit) {
        db.collection("scoreboard")
            .orderBy("highestRatio", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val scoreboardItems = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(ScoreboardItem::class.java)
                }
                resultListener(scoreboardItems)
            }
    }

    fun updateScoreboardItem(userId: String, userName: String, highestRatio: Double) {
        db.collection("scoreboard").document(userId).set(
            ScoreboardItem(userId, userName, highestRatio)
        )
    }
}