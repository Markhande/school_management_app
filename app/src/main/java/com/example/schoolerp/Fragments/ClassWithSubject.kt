package com.example.schoolerp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Adapter.ClassWithSubjectsAdapter
import com.example.schoolerp.DataClasses.ClassWithSubjects
import com.example.schoolerp.databinding.FragmentClassWithSubjectBinding
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.repository.getSubjectRepository
import com.example.schoolerp.viewmodel.getSubjectViewModel
import com.example.schoolerp.viewmodelfactory.getSubjectViewModelFactory

class ClassWithSubject : Fragment() {
    private lateinit var binding: FragmentClassWithSubjectBinding
    private lateinit var adapter: ClassWithSubjectsAdapter
    private var classList = mutableListOf<ClassWithSubjects>()
    private lateinit var viewModel: getSubjectViewModel
    val toolBox = com.example.helpers.MethodLibrary()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassWithSubjectBinding.inflate(inflater, container, false)
        if (!toolBox.isInternetAvailable(requireContext())){
            toolBox.dataFailedLoad(ClassWithSubject(),requireContext())
        }else{
            initView()
            observeData()
        }
        return binding.root
    }

    private fun initView() {
        // Initialize RecyclerView with LinearLayoutManager
        binding.recyclerclasswithsubject.layoutManager = LinearLayoutManager(requireContext())

        adapter = ClassWithSubjectsAdapter(classList)

        // Set the adapter to the RecyclerView
        binding.recyclerclasswithsubject.adapter = adapter
    }

    private fun observeData() {
        try {
            val apiService = RetrofitHelper.getApiService()
            val repository = getSubjectRepository(apiService)
            val factory = getSubjectViewModelFactory(repository, SchoolId().getSchoolId(requireContext()))
            viewModel = ViewModelProvider(this, factory).get(getSubjectViewModel::class.java)

            // Observe the LiveData from the ViewModel
            viewModel.classWithSubjects.observe(viewLifecycleOwner) { subjects ->
                if (subjects.isEmpty()){
                    toolBox.showConfirmationDialog(
                        context = requireContext(),
                        title = "No subject are available.",
                        message = "Would you like to add a subject now?",
                        positiveButtonText = "YES",
                        positiveButtonAction = { toolBox.fragmentChanger(AssignSubject(), requireContext()) },
                        negativeButtonText = "NO",
                        negativeButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
                        cancelable = false)
                }

                Log.d("ClassWithSubject", "Subjects fetched from ViewModel: $subjects")

                // Initialize counters
                var nullClassNameCount = 0
                var nonNullClassNameCount = 0

                // Check if the fetched data is not empty
                if (subjects.isNotEmpty()) {
                    classList.clear()  // Clear any old data
                    classList.addAll(subjects)  // Add the new data to the list
                    adapter.notifyDataSetChanged()  // Notify the adapter to refresh the view

                    try {
                        // Iterate through the subjects to count null and non-null class_names
                        subjects.forEach {
                            if (it.class_name == null) {
                                nullClassNameCount++  // Increment counter for null class_name
                            } else {
                                nonNullClassNameCount++  // Increment counter for non-null class_name
                            }
                            Log.d("ClassWithSubject", "Data added to adapter: ${it.class_name}")
                        }

                        val totalCount = nullClassNameCount + nonNullClassNameCount

                        // Show a Toast with the total count
                        //Toast.makeText(requireContext(), "Total class_name $totalCount", Toast.LENGTH_SHORT).show()

                        // If all class_name values are null, show a specific Toast
                        if (nonNullClassNameCount == 0) {
                            toolBox.showConfirmationDialog(
                                context = requireContext(),
                                title = "No subject are available.",
                                message = "Would you like to add a Subject now?",
                                positiveButtonText = "Yes",
                                positiveButtonAction = { toolBox.fragmentChanger(AssignSubject(), requireContext()) },
                                negativeButtonText = "Cancel",
                                negativeButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
                                cancelable = false)
                            //Toast.makeText(requireContext(), "All class_name values are null!", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        toolBox.showConfirmationDialog(
                            context = requireContext(),
                            title = "Warning !",
                            message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                            positiveButtonText = "OK",
                            positiveButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
                            negativeButtonText = "",
                            negativeButtonAction = { },
                            cancelable = false
                        )
                        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    Log.d("ClassWithSubject", "Data added to adapter: ${classList.size} items.")
                } else {
                    Log.d("ClassWithSubject", "No subjects found.")
                }
            }

        } catch (e: Exception) {
            toolBox.showConfirmationDialog(
                context = requireContext(),
                title = "Warning !",
                message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                positiveButtonText = "OK",
                positiveButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
                negativeButtonText = "",
                negativeButtonAction = { },
                cancelable = false
            )
        }
    }



}

