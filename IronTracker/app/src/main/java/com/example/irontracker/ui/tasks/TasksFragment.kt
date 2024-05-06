package com.example.irontracker.ui.tasks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.irontracker.R
import com.example.irontracker.databinding.FragmentTasksBinding
import com.example.irontracker.databinding.ItemTaskBinding
import com.example.irontracker.ui.data.TaskItem
import com.example.irontracker.ui.visualization.VisualizationViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var visualizationViewModel: VisualizationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        visualizationViewModel = ViewModelProvider(requireActivity())[VisualizationViewModel::class.java]
        tasksViewModel = ViewModelProvider(this,
            TasksViewModel.TasksViewModelFactory(visualizationViewModel)
        )[TasksViewModel::class.java]

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.taskList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe the task data from the ViewModel
        tasksViewModel.taskData.observe(viewLifecycleOwner) { taskItems ->
            val adapter = TaskAdapter(taskItems)
            recyclerView.adapter = adapter
        }

        val addTaskButton: FloatingActionButton = binding.fabAddTask
        addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }

        return root
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val taskNameEditText: EditText = dialogView.findViewById(R.id.edit_task_name)
        val taskWeightEditText: EditText = dialogView.findViewById(R.id.edit_task_weight)
        val currentWeightEditText: EditText = dialogView.findViewById(R.id.edit_current_weight)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val taskName = taskNameEditText.text.toString()
                val taskWeight = taskWeightEditText.text.toString().toDoubleOrNull()
                val currentWeight = currentWeightEditText.text.toString().toDoubleOrNull()

                if (taskName.isNotEmpty() && taskWeight != null && currentWeight != null) {
                    val taskOrder = tasksViewModel.getNextTaskOrder()
                    val taskItem = TaskItem(taskName, taskWeight, currentWeight, taskOrder)
                    tasksViewModel.addTask(taskItem)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showRemoveTaskConfirmationDialog(taskItem: TaskItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove Task")
            .setMessage("Are you sure you want to remove this task?")
            .setPositiveButton("Remove") { _, _ ->
                tasksViewModel.removeTask(taskItem)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    inner class TaskAdapter(private val taskItems: List<TaskItem>) :
        RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val taskItem = taskItems[position]
            holder.bind(taskItem)
        }

        override fun getItemCount() = taskItems.size

        inner class ViewHolder(private val binding: ItemTaskBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun bind(taskItem: TaskItem) {
                binding.taskNumber.text = taskItem.order.toString()
                binding.taskName.text = taskItem.taskName
                binding.taskWeight.text = "Task Weight: ${taskItem.taskWeight}"
                binding.bodyWeight.text = "Body Weight: ${taskItem.currentWeight}"
                binding.ratio.text = "Ratio: ${taskItem.taskWeight / taskItem.currentWeight}"
            }

            init {
                // Set up long click listener for removing a task
                binding.root.setOnLongClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val taskItem = taskItems[position]
                        showRemoveTaskConfirmationDialog(taskItem)
                    }
                    true
                }
            }
        }
    }
}