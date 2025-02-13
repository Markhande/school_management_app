package com.example.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.TimeTableData
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentStudentTimetableBinding
import com.example.schoolerp.repository.GetTimeTableRepository
import com.example.schoolerp.viewmodel.TimeTableViewModel
import com.example.schoolerp.viewmodelfactory.GetTimeTableViewModelFactory
import com.example.student.Adapter.getStudentTimeTableAdapter

class StudentTimetable : Fragment() {
    private lateinit var binding: FragmentStudentTimetableBinding
    private lateinit var viewModelGetTimeTable: TimeTableViewModel
    private lateinit var adapter: getStudentTimeTableAdapter
    private var getTimeTableList: List<TimeTableData> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentTimetableBinding.bind(inflater.inflate(R.layout.fragment_student_timetable, null))
        setupViewModel()
        return binding.root
    }

    private fun setupViewModel() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetTimeTableRepository(apiService)
        val factory = GetTimeTableViewModelFactory(repository)
        viewModelGetTimeTable = ViewModelProvider(this, factory)[TimeTableViewModel::class.java]
        observal()
    }

    private fun observal(){
        viewModelGetTimeTable.timeTableData.observe(viewLifecycleOwner) { result ->
            when {
                result.isSuccess -> {
                    val data = result.getOrNull()?.data ?: emptyList()
                    adapter.updateData(data)
                }
                result.isFailure -> {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
//                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    dataFailedLoad()
                }
            }
        }
        viewModelGetTimeTable.fetchTimeTable(SchoolId().getSchoolId(requireContext()))
        initView()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, true
        )
        layoutManager.stackFromEnd=true
        binding.GetStudentTimeTableRecycler.layoutManager=layoutManager
        adapter = getStudentTimeTableAdapter(getTimeTableList)
        binding.GetStudentTimeTableRecycler.adapter = adapter
    }

    private fun dataFailedLoad(){
        binding.tvDataNotFound.visibility = View.VISIBLE
    }
}