package com.example.irontracker.ui.visualization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.irontracker.databinding.FragmentVisualizationBinding
import com.example.irontracker.ui.data.TaskItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class VisualizationFragment : Fragment() {

    private var _binding: FragmentVisualizationBinding? = null
    private val binding get() = _binding!!

    private lateinit var visualizationViewModel: VisualizationViewModel
    private lateinit var chart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisualizationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        chart = binding.chart
        configureChart()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sampleData = listOf(
            TaskItem("Task 1", 150.0, 220.0),
            TaskItem("Task 2", 122.0, 322.0),
            TaskItem("Task 3", 300.0, 300.0)
        )

        updateChart(sampleData)
    }

    private fun loadData() {
        visualizationViewModel.taskData.observe(viewLifecycleOwner) { taskItems ->
            updateChart(taskItems)
        }
    }

    private fun configureChart() {
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.axisMinimum = 0f

        val rightAxis: YAxis = chart.axisRight
        rightAxis.isEnabled = false
    }

    private fun updateChart(taskItems: List<TaskItem>) {
        val ratioEntries = taskItems.mapIndexed { index, taskItem ->
            val ratio = if (taskItem.currentWeight > 0) taskItem.taskWeight / taskItem.currentWeight else 0f
            Entry(index.toFloat(), ratio.toFloat())
        }

        if (ratioEntries.isNotEmpty()) {
            val dataSet = LineDataSet(ratioEntries, "Weight/Body Weight Ratio")
            dataSet.lineWidth = 5f
            dataSet.circleRadius = 4f
            dataSet.setDrawValues(false)
//            line color
            dataSet.color = resources.getColor(android.R.color.holo_blue_light, null)

            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()
        } else {
            chart.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}