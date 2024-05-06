package com.example.irontracker.ui.tasks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.irontracker.ui.data.FirebaseRepository
import com.example.irontracker.ui.data.TaskItem
import com.example.irontracker.ui.data.TaskRepository
import com.example.irontracker.ui.visualization.VisualizationViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TasksViewModel(private val visualizationViewModel: VisualizationViewModel) : ViewModel() {

    private val _taskData = MutableLiveData<List<TaskItem>>()
    val taskData: LiveData<List<TaskItem>> = _taskData
    private val firebaseRepository = FirebaseRepository()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val taskRepository = TaskRepository()
    class TasksViewModelFactory(private val visualizationViewModel: VisualizationViewModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TasksViewModel(visualizationViewModel) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    init {
        fetchTaskData()
    }

    private fun fetchTaskData() {
        viewModelScope.launch {
            taskRepository.getAllTasks { tasks ->
                Log.d("TasksViewModel", "Fetched tasks: $tasks")
                _taskData.value = tasks
                visualizationViewModel.updateTaskData(tasks)
            }
        }
    }

    fun addTask(taskItem: TaskItem) {
        val currentList = _taskData.value ?: emptyList()
        val taskOrder = getNextTaskOrder()
        val updatedTaskItem = taskItem.copy(userId = currentUser?.uid ?: "", order = taskOrder)
        val updatedList = currentList + updatedTaskItem
        _taskData.value = updatedList
        visualizationViewModel.updateTaskData(updatedList)

        // Upload the task item to Firebase
        firebaseRepository.uploadTaskItem(updatedTaskItem)

        // Update the scoreboard
        val highestRatio = updatedList.maxOfOrNull { it.taskWeight / it.currentWeight } ?: 0.0
        currentUser?.let { user ->
            firebaseRepository.updateScoreboardItem(user.uid, user.displayName ?: "", highestRatio)
        }

        // Add the task to the TaskRepository
        taskRepository.addTask(updatedTaskItem)
    }

    fun removeTask(taskItem: TaskItem) {
        val currentList = _taskData.value ?: emptyList()
        val updatedList = currentList.filter { it != taskItem }
        updatedList.forEachIndexed { index, task -> task.order = index + 1 }
        _taskData.value = updatedList
        visualizationViewModel.updateTaskData(updatedList)
    }

    fun getNextTaskOrder(): Int {
        val currentList = _taskData.value ?: emptyList()
        return currentList.size + 1
    }
}