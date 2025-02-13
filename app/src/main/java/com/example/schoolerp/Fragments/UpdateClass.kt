package com.example.schoolerp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.helpers.MethodLibrary
import com.example.helpers.validation
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.ClassItem
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentUpdateClassBinding
import com.example.schoolerp.models.responses.TeacherNameResponse
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateClass : Fragment() {

    private lateinit var binding: FragmentUpdateClassBinding
    val toolBox = MethodLibrary()
    val validator = validation()
    private lateinit var viewModelAllClassAssignSubejct: AllClassViewModel
    private lateinit var classItem: ClassItem
    private lateinit var viewModel: AllClassViewModel
    private val classNamesList = mutableListOf<String>()
    private lateinit var defaultNameSp :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateClassBinding.inflate(inflater, container, false)

        try{
            // Retrieve data passed from previous fragment
            arguments?.getSerializable("class_data")?.let {
                classItem = it as ClassItem
                displayClassDetails(classItem)
            }

            // Initialize ViewModel
            val repository = AllClassRepository()
            val factory = AllClassViewModelFactory(repository)
            viewModel = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)

            // You can get schoolId from SharedPreferences if needed
            val classId = classItem.class_id // Use the classId from classItem

            // Set the default values for the fields (you can edit these)
            binding.tvClassTwo.setText(classItem.class_name)
            binding.tvTutionFeesTwo.setText(classItem.tution_fees)
            defaultNameSp = classItem.class_teacher
//        binding.tvFeesDiscount.setText(classItem.tution_fees)

            // When the "Update" button is clicked, call the update API
            binding.btnUpdateClass.setOnClickListener {
                if (!binding.spAllTeacherName.selectedItem.toString().equals("Select Teacher")){
                    val updatedClassName = binding.tvClassTwo.text.toString()
                    val updatedTutionFees = binding.tvTutionFeesTwo.text.toString()
                    val updatedClassTeacher = binding.spAllTeacherId.selectedItem.toString()

                    // Map the updated details to pass in the API request
                    val classDetails = mapOf(
                        "class_name" to updatedClassName,
                        "tution_fees" to updatedTutionFees,
                        "class_teacher" to updatedClassTeacher,
                        "school_id" to SchoolId().getSchoolId(requireContext())
                    )

                    // Trigger the update API call
                    viewModel.updateClass(SchoolId().getSchoolId(requireContext()), classId, classDetails).observe(viewLifecycleOwner) { response ->
                        if (response.status) {
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                            toolBox.fragmentChanger(AllClass(), requireContext())
//                        toolBox.showConfirmationDialog(
//                            context = requireContext(), // or requireContext() if in a Fragment
//                            title = "Class Updated Successfully",
//                            positiveButtonText = "OK",
//                            positiveButtonAction = {  },
//                            negativeButtonText = "",
//                            negativeButtonAction = { },
//                            cancelable = true )

                        } else {
                            Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(requireContext(), "Please Select Teacher", Toast.LENGTH_SHORT).show()
                }

            }
            fetchTeacherNames(SchoolId().getSchoolId(requireContext()))
        }catch (E : Exception){
            Toast.makeText(requireContext(), E.message, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun displayClassDetails(classItem: ClassItem) {
        binding.tvClassTwo.setText(classItem.class_name)
        binding.tvTutionFeesTwo.setText(classItem.total_students)
        binding.tvClassTeacher.setText(classItem.total_boys)

    }

    private fun fetchTeacherNames(SchoolID: String) {
        try {
            RetrofitHelper.getApiService().getEmployeeData(SchoolID).enqueue(object : Callback<TeacherNameResponse> {
                override fun onResponse(call: Call<TeacherNameResponse>, response: Response<TeacherNameResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val employees = response.body()!!.data
                        val roles = mutableListOf<String>()
                        roles.add("Select Teacher")

                        // Create a map to store employee names and their corresponding IDs
                        val teacherIdMap = mutableMapOf<String, String>()

                        // Extracting employee names and populating the roles list
                        for (employee in employees) {
                            employee.employeeName?.let {
                                roles.add(it)  // Add employee names to the list
                                // Store the corresponding ID for each employee
                                teacherIdMap[it] = employee.id.orEmpty()
                            }
                        }

                        if (roles.isEmpty()) {
                            Toast.makeText(requireContext(), "No employee names available", Toast.LENGTH_SHORT).show()
                        }

                        // Set up the adapter to populate the Teacher Spinner
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spAllTeacherName.adapter = adapter

                        // Automatically select the default teacher if the name matches any in the list
                        val defaultPosition = roles.indexOf(defaultNameSp)
                        if (defaultPosition > 0) {  // Position > 0 to avoid "Select Teacher"
                            binding.spAllTeacherName.setSelection(defaultPosition)
                        }

                        // Set the onItemSelectedListener for spinner interaction
                        binding.spAllTeacherName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                // Check if a valid teacher is selected (position > 0 to avoid "Select Teacher")
                                if (position > 0) {
                                    val selectedTeacher = roles[position] // The selected teacher name
                                    val teacherId = teacherIdMap[selectedTeacher] // Get the corresponding teacher ID
                                    // Call a function to populate the second spinner with teacher IDs
                                    populateSecondSpinner(teacherId)
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // Handle case when nothing is selected
                            }
                        }

                    } else {
                        Toast.makeText(requireContext(), "Failed to load data: ${response.message()}", Toast.LENGTH_SHORT).show()
                        Log.e("NewClass", "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<TeacherNameResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("NewClass", "Network error: ${t.message}")
                }
            })
        } catch (E: Exception) {
            Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show()
        }


    }

    private fun populateSecondSpinner(teacherId: String?) {
        if (teacherId != null) {
            val idList = listOf(teacherId)
            // Set up the adapter for the second spinner (etClassTeacher2)
            val idAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, idList)
            idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spAllTeacherId.adapter = idAdapter
            binding.spAllTeacherId.setSelection(0)
        } else {
            Toast.makeText(requireContext(), "Invalid Teacher ID", Toast.LENGTH_SHORT).show()
        }
    }

}