// TaskItem.kt
package com.example.irontracker.ui.data

data class TaskItem @JvmOverloads constructor(
    val taskName: String = "",
    val taskWeight: Double = 0.0,
    val currentWeight: Double = 0.0,
    var order: Int = 0,
    val userId: String = ""
)