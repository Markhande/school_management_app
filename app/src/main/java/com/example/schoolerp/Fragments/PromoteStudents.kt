package com.example.schoolerp.Fragments.Fragment

import com.example.student.Adapter.PromoteStudentAdapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentPromoteStudentsBinding
import com.example.schoolerp.models.responses.PromoteStudentRequest
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.getStudentRepository
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.getStudentViewModel
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.getStudentViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PromoteStudents : Fragment() {
    private lateinit var binding: FragmentPromoteStudentsBinding
    private lateinit var adapter: PromoteStudentAdapter
    private lateinit var getAllClassName: AllClassViewModel
    private lateinit var getAllStudentViewModel: getStudentViewModel
    private lateinit var clsaaId: String
    private lateinit var classFilterId: String
    private var allStudents: List<Student> = mutableListOf()
    val toolBox = MethodLibrary()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromoteStudentsBinding.inflate(inflater, container, false)

        initializeViewModels()
        getClasses()
        getStudents()
        eventLisnearActive()

        binding.refreshImageLayout.setOnClickListener {
            toolBox.rotateImage(binding.refreshImage)
            getClasses()
            getStudents()
        }
        return binding.root
    }

    private fun promoteStudents(promoteRequest: PromoteStudentRequest) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.getApiService().promoteStudents(promoteRequest)
                if (response.isSuccessful) {
                    //val apiResponse = response.body()
                    withContext(Dispatchers.Main) {
                        if (response.body()?.message != null) {
                            toolBox.showConfirmationDialog(
                                context = requireContext(),
                                title = "Student are promoted",
                                message = "would you like to promote more students ? ",
                                positiveButtonText = "yes",
                                positiveButtonAction = { toolBox.fragmentChanger(PromoteStudents(), requireContext()) },
                                negativeButtonText = "no",
                                negativeButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
                                cancelable = false)
//                            Toast.makeText(requireContext(), apiResponse.message, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to promote students", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initializeViewModels() {
        // Initialize ViewModels
        //val classFactory = AllClassViewModelFactory(AllClassRepository())
        getAllClassName = ViewModelProvider(this, AllClassViewModelFactory(AllClassRepository()) ).get(AllClassViewModel::class.java)

        //val studentFactory = getStudentViewModelFactory(getStudentRepository(RetrofitHelper.getApiService()))
        getAllStudentViewModel = ViewModelProvider(this, getStudentViewModelFactory(getStudentRepository(RetrofitHelper.getApiService()))).get(getStudentViewModel::class.java)

        // Fetch students by School ID
        getAllStudentViewModel.fetchStudentsBySchoolId(SchoolId().getSchoolId(requireContext()))
    }

    private fun setupRecyclerView(students: List<Student>) {
        // Set up the adapter with the list of students
        adapter = PromoteStudentAdapter(students, object : PromoteStudentAdapter.OnItemClickListener {
            override fun onItemClick(item: String, position: Int) {
                // Handle item click (e.g., show more details of the student)
               // Toast.makeText(requireContext(), "Clicked: $item", Toast.LENGTH_SHORT).show()
            }
        }, object : PromoteStudentAdapter.NumberClickListener {
            override fun onClickedNumber(item: String, position: Int, studentId: String) {
                // Handle checkbox click (e.g., log the student ID)
                Log.d("PromoteStudents", "Student ID: $studentId is selected")
            }
        })

        binding.recyclerViewPromoteStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPromoteStudents.adapter = adapter

        // To get the selected student IDs later, you can do this:
        val selectedStudentIds = adapter.getSelectedStudentIds()
        Log.d("PromoteStudents", "Selected Student IDs: $selectedStudentIds")

//        Filter the Spinner by the selected students
    }

    private fun getClasses() {
        try {
            getAllClassName.getClasses(SchoolId().getSchoolId(requireContext())).observe(viewLifecycleOwner) { response ->
                if (response != null && response.status) {
                    // Add "Select Class" as the first item in the classNames list
                    val classNames = mutableListOf("Select Class")  // Add default "Select Class"
                    val classIds = mutableListOf<String>()  // Add a placeholder for classIds

                    // Populate classNames and classIds with actual data
                    response.data.forEach {
                        classNames.add(it.class_name)
                        classIds.add(it.class_id)
                    }

                    // Set up the ArrayAdapter for both spinners
                    val classAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, classNames)
                    binding.spSelectClass.adapter = classAdapter
                    binding.spSelectClassFilter.adapter = classAdapter

                    // Spinner for selecting class
                    binding.spSelectClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                            if (position > 0) {  // Skip the default "Select Class" item
                                val selectedClassId = classIds[position - 1] // Adjust index to skip "Select Class"
                                clsaaId = selectedClassId
                                Log.d("PromoteStudents", "Selected Class ID: $selectedClassId")
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                    // Spinner for selecting class filter
                    binding.spSelectClassFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                            if (position > 0) {  // Skip the default "Select Class" item
                                // Get the selected class ID from the spinner
                                val selectedClassId = classIds[position - 1]
                                classFilterId = selectedClassId

                                // Log the selected class ID
                                Log.d("PromoteStudents", "Selected Class ID: $classFilterId")

                                // Now filter the students based on the selected class ID
                                filterStudentsByClassId()

                                // Optionally, call getStudents() or other necessary methods
                                getStudents()
                                binding.recyclerViewPromoteStudents.visibility = View.VISIBLE
                                binding.promoteLayout.visibility = View.VISIBLE
                            }else{
                                binding.recyclerViewPromoteStudents.visibility = View.GONE
                                binding.promoteLayout.visibility = View.GONE
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Optionally, handle the case when nothing is selected, maybe reset the filter
                            filterStudentsByClassId() // Filter students without any specific class filter
                        }
                    }

                } else {
                    Log.d("PromoteStudents", "No classes found")
                }
            }
        } catch (e: Exception) {
            // Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("PromoteStudents", "Error: ${e.message}")
        }
    }



    private fun getStudents() {
        // Observe the list of students
        getAllStudentViewModel.students.observe(viewLifecycleOwner) { students ->
            if (students != null) {
                allStudents = students  // Store all students
                filterStudentsByClassId()  // Filter students when the list is fetched
            }
        }
    }

    private fun filterStudentsByClassId() {
        try {
            val filteredStudents = allStudents.filter { it.st_class == classFilterId }
            setupRecyclerView(filteredStudents)
        }catch (e: Exception){
            //Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun eventLisnearActive(){
        // Set up the submit button
        binding.btnSubmit.setOnClickListener {
                try {
                    val selectedStudentIds = adapter.getSelectedStudentIds()
                    val newClass = clsaaId.toInt()

                    if (selectedStudentIds.isNotEmpty()) {
                        if (!binding.spSelectClassFilter.selectedItem.equals("Select Class")) {
                            if(binding.spSelectClassFilter.selectedItem.toString() !=  binding.spSelectClass.selectedItem.toString()){
                                val promoteRequest = PromoteStudentRequest(
                                    student_ids = selectedStudentIds.map { it.toInt() }, // Convert IDs to a list of Integers
                                    new_class = newClass
                                )
                                // Call the API
                                promoteStudents(promoteRequest)
                            }else{
                                toolBox.showConfirmationDialog(
                                    context = requireContext(),
                                    title = "Student already present in the selected class",
//                                    message = "please choose a new class",
                                    positiveButtonText = "ok",
                                    positiveButtonAction = {  },
                                    negativeButtonText = "",
                                    negativeButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
                                    cancelable = true)
                            }

                        }else{
                            Toast.makeText(requireContext(), "please select class for promote", Toast.LENGTH_SHORT).show()
                        }
                        // Prepare the request body

                    } else {
                        Toast.makeText(requireContext(), "No students selected", Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
//                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    Toast.makeText(requireContext(), "please select class for promote", Toast.LENGTH_SHORT).show()
                    Log.e("PromoteStudents", "Error: ${e.message}")
                }

        }

    }
}

