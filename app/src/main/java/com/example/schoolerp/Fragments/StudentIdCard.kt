package com.example.schoolerp.Fragments.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolerp.Adapter.StudentIdDetailsAdapter
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentStudentIdCardBinding
import com.example.schoolerp.viewmodel.StudentIdCardViewModel
import com.example.schoolerp.models.responses.StudentIdCardDetails

class StudentIdCard : Fragment() {

    private lateinit var binding: FragmentStudentIdCardBinding
    private lateinit var studentIdDetailsAdapter: StudentIdDetailsAdapter
    private lateinit var viewModel: StudentIdCardViewModel

    private lateinit var fullStudentList: List<StudentIdCardDetails> // Store the full list
    private var filteredList: MutableList<StudentIdCardDetails> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentIdCardBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(StudentIdCardViewModel::class.java)

        // Initialize RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStudentIdCard.layoutManager = layoutManager
        studentIdDetailsAdapter = StudentIdDetailsAdapter(emptyList()) // Set an empty list initially
        binding.recyclerViewStudentIdCard.adapter = studentIdDetailsAdapter

        // Observe LiveData from ViewModel for student data
        viewModel.studentList.observe(viewLifecycleOwner) { studentList ->
            if (studentList.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No students found", Toast.LENGTH_SHORT).show()
            } else {
                fullStudentList = studentList // Save the full list
                filteredList.clear()
                filteredList.addAll(fullStudentList) // Initialize filtered list with full list
                studentIdDetailsAdapter = StudentIdDetailsAdapter(filteredList)
                binding.recyclerViewStudentIdCard.adapter = studentIdDetailsAdapter
            }
        }

        // Observe error messages from ViewModel
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
        }

        // Fetch student data from API
        fetchStudentIdCardData()

        // Set up the search functionality
        setupSearchView()

        return binding.root
    }

    private fun fetchStudentIdCardData() {
        val schoolId = SchoolId().getSchoolId(requireContext())
        viewModel.fetchStudentIdCardData(schoolId)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // We don't need to handle query submission
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterStudents(newText)
                return true
            }
        })
    }

    private fun filterStudents(query: String?) {
        val filtered = if (query.isNullOrEmpty()) {
            fullStudentList
        } else {
            fullStudentList.filter {
                it.st_name?.contains(query, ignoreCase = true) == true
            }
        }

        filteredList.clear()
        filteredList.addAll(filtered)
        studentIdDetailsAdapter.notifyDataSetChanged()
    }
}
