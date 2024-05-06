package com.example.irontracker.ui.visualization

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.irontracker.ui.data.TaskItem

class VisualizationViewModel : ViewModel() {
    private val _taskData = MutableLiveData<List<TaskItem>>()
    val taskData: LiveData<List<TaskItem>> = _taskData

    fun updateTaskData(taskItems: List<TaskItem>) {
        Log.d("VisualizationViewModel", "Updating taskData: $taskItems")
        _taskData.value = taskItems
    }
}