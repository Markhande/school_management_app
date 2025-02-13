package com.example.schoolerp.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ListItemStudentNameBinding

class StudentNamesAdapter(
    val studentNames: List<Student>, // List of students
    private val onAttendanceChanged: (String, String) -> Unit // Callback for attendance change
) : RecyclerView.Adapter<StudentNamesAdapter.StudentNamesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentNamesViewHolder {
        val binding = ListItemStudentNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentNamesViewHolder(binding, onAttendanceChanged)
    }

    override fun onBindViewHolder(holder: StudentNamesViewHolder, position: Int) {
        val student = studentNames[position]
        holder.bind(student.st_name, student.attendance_status, position, student.id)
    }

    override fun getItemCount(): Int = studentNames.size

    class StudentNamesViewHolder(
        private val binding: ListItemStudentNameBinding,
        private val onAttendanceChanged: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val attendanceMap = mutableMapOf<String, String>()

        fun bind(studentName: String, studentAttences: String?, position: Int, studentID: String) {
            binding.studentNameText.text = studentName
            binding.StdID.text = studentID

            // Set background color based on position
            val color = if (position % 2 == 0) {
                ContextCompat.getColor(itemView.context, R.color.white)
            } else {
                ContextCompat.getColor(itemView.context, R.color.info_bg)
            }
            binding.listItemBody.setBackgroundColor(color)

            // Reset button styles before setting the attendance status
            resetButtonStyles()

            // Set the initial state of the buttons based on the attendance status
            when (studentAttences) {
                "P" -> {
                    binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selected_p)
                    binding.btnPresent.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                    binding.btnPresent.setTypeface(null, Typeface.BOLD)
                }
                "A" -> {
                    binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selected_a)
                    binding.btnAbsent.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                    binding.btnAbsent.setTypeface(null, Typeface.BOLD)
                }
                "L" -> {
                    binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selected_l)
                    binding.btnLeave.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                    binding.btnLeave.setTypeface(null, Typeface.BOLD)
                }
                else -> {
                    // Default to "A" if no status is set
                    binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selected_a)
                    binding.btnAbsent.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                    binding.btnAbsent.setTypeface(null, Typeface.BOLD)
                }
            }

            binding.btnPresent.setOnClickListener {
                resetButtonStyles()
                binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selected_p)
                binding.btnPresent.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.btnPresent.setTypeface(null, Typeface.BOLD)

                // Update the attendanceMap with the new status
                attendanceMap[studentID] = "P"
                onAttendanceChanged(studentID, "P") // Callback to update the UI or any other logic
            }

            binding.btnAbsent.setOnClickListener {
                resetButtonStyles()
                binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selected_a)
                binding.btnAbsent.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.btnAbsent.setTypeface(null, Typeface.BOLD)

                // Update the attendanceMap with the new status
                attendanceMap[studentID] = "A"
                onAttendanceChanged(studentID, "A") // Callback to update the UI or any other logic
            }

            binding.btnLeave.setOnClickListener {
                resetButtonStyles()
                binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selected_l)
                binding.btnLeave.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.btnLeave.setTypeface(null, Typeface.BOLD)

                // Update the attendanceMap with the new status
                attendanceMap[studentID] = "L"
                onAttendanceChanged(studentID, "L") // Callback to update the UI or any other logic
            }
        }

        private fun resetButtonStyles() {
            val textColor = ContextCompat.getColor(itemView.context, R.color.black)
            binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selector)
            binding.btnPresent.setTextColor(textColor)
            binding.btnPresent.setTypeface(null, Typeface.NORMAL)
            binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selector)
            binding.btnAbsent.setTextColor(textColor)
            binding.btnAbsent.setTypeface(null, Typeface.NORMAL)
            binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selector)
            binding.btnLeave.setTextColor(textColor)
            binding.btnLeave.setTypeface(null, Typeface.NORMAL)
        }
    }
}

