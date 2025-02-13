package com.example.schoolerp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.StudentAttendanceReportAdapter
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentStudentAttendanceReportBinding
import com.example.schoolerp.repository.StudentAttendanceReportRepository
import com.example.schoolerp.viewmodel.StudentAttendanceReportViewModel
import com.example.schoolerp.viewmodelfactory.StudentAttendanceReportViewModelFactory

class StudentAttendanceReport : Fragment() {
    private lateinit var binding: FragmentStudentAttendanceReportBinding
    private lateinit var viewModel: StudentAttendanceReportViewModel
    private lateinit var adapter: StudentAttendanceReportAdapter
    val toolBox = MethodLibrary()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentAttendanceReportBinding.inflate(inflater, container, false)

        // Initialize ViewModel and Repository
        val apiService = RetrofitHelper.getApiService()
        val repository = StudentAttendanceReportRepository(apiService)
        val factory = StudentAttendanceReportViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(StudentAttendanceReportViewModel::class.java)

        // Initialize RecyclerView Adapter
        adapter = StudentAttendanceReportAdapter(emptyList())
        binding.recyclerViewStudentNames.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStudentNames.adapter = adapter

        // Observe the attendance data from ViewModel
        viewModel.attendanceReport.observe(viewLifecycleOwner, Observer { response ->
            try{
                if (response != null && response.status) {
                    Toast.makeText(requireContext(), "Successfully loaded data", Toast.LENGTH_SHORT).show()

                    // Pass response.data to the adapter
                    adapter.updateData(response.data)

                    // Logging the data for debugging
                    val studentNames = adapter.getStudentNames()
                    studentNames.forEach { name ->
                        Log.d("StudentNames", name)
                    }

                    val classNames = adapter.getClassNames()
                    classNames.forEach { name ->
                        Log.d("ClassNames", name)
                    }

                    val serialNumbers = adapter.getSerialNumber()
                    serialNumbers.forEach { num ->
                        Log.d("SerialNumbers", num.toString())
                    }

                    val attendanceDetails = adapter.getAttendanceDetails()
                    attendanceDetails.forEach { detail ->
                        Log.d("AttendanceDetails", detail)
                    }
                } else {
                    // Handle empty or failed data response
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
        // Fetch the student attendance data when fragment is created
        viewModel.fetchStudentAttendanceReport(SchoolId().getSchoolId(requireContext()), "")

        return binding.root
    }
}





