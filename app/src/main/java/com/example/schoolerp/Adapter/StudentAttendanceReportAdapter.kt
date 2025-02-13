package com.example.schoolerp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemStudentattendancereportBinding
import com.example.schoolerp.models.responses.Item
import com.example.schoolerp.models.responses.StudentAttendanceReportResponse

class StudentAttendanceReportAdapter(private var dataList: List<StudentAttendanceReportResponse.Data>) :
    RecyclerView.Adapter<StudentAttendanceReportAdapter.AttendanceViewHolder>() {

    // ViewHolder that binds the data to the views
    inner class AttendanceViewHolder(private val binding: ItemStudentattendancereportBinding) :
        RecyclerView.ViewHolder(binding.root) {
            var listItemBody:LinearLayout = binding.listItemBody

        @SuppressLint("SetTextI18n")
        fun bind(data: StudentAttendanceReportResponse.Data) {
            // Bind student name and class name to the UI
            binding.studentName.text = data.student_name+" "
            binding.className.text = data.class_name

            // Dynamically add attendance records in a horizontal scrollable layout
            val attendanceLayout = binding.attendanceLayout
            attendanceLayout.removeAllViews() // Remove previous views

            // Loop through attendance data and create TextViews
            data.attendance.forEach { attendance ->
                // Create a new LinearLayout to hold both the date and status vertically
                val attendanceItemLayout = LinearLayout(binding.root.context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 0, 16, 0) // Add padding
                }

                // Create TextView for the date
                val dateTextView = TextView(binding.root.context).apply {
                    text = attendance.date.substring(8)
                    textSize = 14f
                    setPadding(0, 8, 0, 0) // Top padding for spacing
                }

                // Create TextView for the status (Present/Absent)
                val statusTextView = TextView(binding.root.context).apply {
                    text = attendance.status
                    textSize = 16f
                    setPadding(0, 8, 0, 0) // Add some spacing below the date
                }

                // Add the TextViews to the item layout
                attendanceItemLayout.addView(dateTextView)
                attendanceItemLayout.addView(statusTextView)

                // Add the item layout to the horizontal layout (this allows horizontal scrolling)
                attendanceLayout.addView(attendanceItemLayout)
            }
        }
    }

    // Create new ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemStudentattendancereportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    // Bind data to the ViewHolder at the given position
    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val color = if (position % 2 == 0) {
            ContextCompat.getColor(holder.itemView.context, R.color.info_bg)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.white)
        }
        holder.listItemBody.setBackgroundColor(color)
        val data = dataList[position]
        holder.bind(data)
    }

    // Return the number of items in the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Update data in the adapter
    fun updateData(newData: List<StudentAttendanceReportResponse.Data>) {
        dataList = newData
        notifyDataSetChanged()  // Notify RecyclerView that data has been updated
    }

    // Getter methods for specific data
    fun getStudentNames(): List<String> {
        return dataList.map { it.student_name }
    }

    fun getClassNames(): List<String> {
        return dataList.map { it.class_name }
    }

    fun getSerialNumber(): List<Int> {
        return dataList.map { it.serial_no }
    }

    fun getAttendanceDetails(): List<String> {
        return dataList.flatMap { data ->
            data.attendance.map { attendance ->
                "Date: ${attendance.date}, Status: ${attendance.status}"
            }
        }
    }
}




