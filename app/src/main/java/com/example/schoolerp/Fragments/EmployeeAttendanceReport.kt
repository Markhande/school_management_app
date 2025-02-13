package com.example.schoolerp.Fragments

import android.content.ClipData.Item
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.EmployeeAttendanceReportAdapter
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentEmployeeAttendanceReportBinding
import com.example.schoolerp.databinding.FragmentStudentFeeReceiptBinding
import com.example.student.Adapter.studentFeeReceiptAdapter

class EmployeeAttendanceReport : Fragment() {
    private lateinit var binding: FragmentEmployeeAttendanceReportBinding
    private lateinit var adapter: EmployeeAttendanceReportAdapter
    val toolBox = MethodLibrary()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeeAttendanceReportBinding.bind(inflater.inflate(R.layout.fragment_employee_attendance_report, null))


        val items = listOf(
            Item("Hemant Ramesh Markhande", "Teacher"),
            Item("Lokesh Suresh Puri", "Teacher"),
            Item("Nayan shyam Pote", "Teacher"),
            Item("Suyash jivan Gutkhe", "Teacher"),
            Item("Divya waman Raut", "Teacher"),
            Item("Darshana Rajendra Nasare", "Teacher"),
        ).toSet().toList()

        binding.recyclerViewEmployeeNames.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewEmployeeNames.adapter = EmployeeAttendanceReportAdapter(items)
        return binding.root
    }
}