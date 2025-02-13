package com.example.schoolerp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentMarksStudentAttendanceAllClassStudentBinding

class MarksStudentAttendanceAllClassStudent : Fragment() {
    private lateinit var binding: FragmentMarksStudentAttendanceAllClassStudentBinding
    private var className: String? = null
    private var selectedDate: String? = null
    private var studentNames: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMarksStudentAttendanceAllClassStudentBinding.bind(
            inflater.inflate(R.layout.fragment_marks_student_attendance_all_class_student, container, false)
        )

        // Retrieve class name, selected date, and student names from arguments
        className = arguments?.getString("className")
        selectedDate = arguments?.getString("selectedDate")
        studentNames = arguments?.getString("studentNames")

        // Display the data in the UI
        displayClassData(className, selectedDate, studentNames)

        return binding.root
    }

    private fun displayClassData(className: String?, selectedDate: String?, studentNames: String?) {
        if (className != null && selectedDate != null && studentNames != null) {
            // Display class name, selected date, and student names in the TextViews
            binding.classNameText.text = "Class: $className"
         //   binding.selectedDateText.text = "Date: $selectedDate"
           // binding.studentNamesText.text = "Students: $studentNames"  // Assuming you have a TextView for student names
        } else {
            binding.classNameText.text = "No class selected"
         //   binding.selectedDateText.text = "No date selected"
          ///  binding.studentNamesText.text = "No students available"
        }
    }
}
