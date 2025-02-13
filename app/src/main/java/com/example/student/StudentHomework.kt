package com.example.student

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.Homework
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentStudentHomeworkBinding
import com.example.schoolerp.repository.GetHomeworkRepository
import com.example.schoolerp.viewmodel.GetHomeworkViewModel
import com.example.schoolerp.viewmodelfactory.GetHomeworkViewModelFactory
import com.example.student.Adapter.getStudentHomeWorkAdapter
import com.example.teacher.TeacherDetails

class StudentHomework : Fragment() {
    private lateinit var binding: FragmentStudentHomeworkBinding
    private lateinit var viewModelGetHomework: GetHomeworkViewModel
    private lateinit var adapter: getStudentHomeWorkAdapter
    private  var itemCount: Int = 0
    private var toolBox=MethodLibrary()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the binding
        binding = FragmentStudentHomeworkBinding.inflate(inflater, container, false)

        setupRecyclerView()

        toolBox.clicked(binding.refreshImageLayout){
            setupRecyclerView()
            toolBox.rotateImage(
                imageView =  binding.refreshImage,
                duration = 1000
            )       }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvGetStudentHomeWork.layoutManager = LinearLayoutManager(context)
        // Initialize the adapter
        adapter = getStudentHomeWorkAdapter(
            requireContext(),
            emptyList(),
            onEditClick = { homework -> onEditClick(homework) },
            onDeleteClick = { homework -> onDeleteClick(homework) }
        )
        binding.rvGetStudentHomeWork.adapter = adapter
        observeHomeworkList()
    }

    private fun observeHomeworkList() {
        // Initialize the ViewModel

        val apiService = RetrofitHelper.getApiService()
        val repository = GetHomeworkRepository(apiService)
        val factory = GetHomeworkViewModelFactory(repository)
        viewModelGetHomework = ViewModelProvider(this, factory)[GetHomeworkViewModel::class.java]
        viewModelGetHomework.getHomeWork(SchoolId().getSchoolId(requireContext()), TeacherDetails().getTeacherId(requireContext()))

        viewModelGetHomework.homeworkList.observe(viewLifecycleOwner) { homeworkList ->

            if (homeworkList != null) {
                if (homeworkList.isNotEmpty()) {
                    itemCount = homeworkList.size
                    saveItemCountToPreferences(itemCount)

                    adapter = getStudentHomeWorkAdapter(
                        requireContext(),
                        homeworkList.reversed(),
                        onEditClick = { homework -> onEditClick(homework) },
                        onDeleteClick = { homework -> onDeleteClick(homework) }
                    )
                    binding.rvGetStudentHomeWork.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "No homework found", Toast.LENGTH_SHORT).show()
                }
            } else {
//                Toast.makeText(requireContext(), "Failed to load homework list", Toast.LENGTH_SHORT).show()
                toolBox.showConfirmationDialog(
                    context = requireContext(),
                    title = "Homework not available",
                    message = "Homework is not assigned yet",
                    positiveButtonText = "OK",
                    positiveButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
                    negativeButtonText = "",
                    negativeButtonAction = { },
                    cancelable = false)
            }
        }
    }

    private fun onEditClick(homework: Homework) {
        // Handle edit click (you can implement this logic)
        Toast.makeText(requireContext(), "Edit clicked for homework: ${homework.id}", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteClick(homework: Homework) {
        // Handle delete click (you can implement this logic)
        Toast.makeText(requireContext(), "Delete clicked for homework: ${homework.id}", Toast.LENGTH_SHORT).show()
    }
    private fun saveItemCountToPreferences(itemCount: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("HomeworkPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("itemCount", itemCount)
        editor.apply()
    }
}
