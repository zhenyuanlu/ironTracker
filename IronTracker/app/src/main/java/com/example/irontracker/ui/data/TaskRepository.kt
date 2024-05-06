package com.example.irontracker.ui.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun getAllTasks(resultListener: (List<TaskItem>) -> Unit) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid).collection("tasks")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tasks = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(TaskItem::class.java)
                    }
                    resultListener(tasks)
                }
                .addOnFailureListener { exception ->
                    // Handle the failure case
                    resultListener(emptyList())
                }
        } ?: run {
            // User is not authenticated
            resultListener(emptyList())
        }
    }

    fun addTask(taskItem: TaskItem) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid).collection("tasks")
                .add(taskItem)
        }
    }
}