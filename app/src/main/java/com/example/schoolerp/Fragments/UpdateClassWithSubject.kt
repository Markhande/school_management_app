package com.example.schoolerp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.Subject
import com.example.schoolerp.Fragments.Fragment.AddStudent
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentUpdateClassWithSubjectBinding
import com.example.schoolerp.models.responses.ClassWithResposne
import com.example.schoolerp.models.responses.DeleteSubjectResponse
import com.example.schoolerp.repository.getSubjectRepository
import com.example.schoolerp.viewmodel.getSubjectViewModel
import com.example.schoolerp.viewmodelfactory.getSubjectViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateClassWithSubject : Fragment() {
    val tooBox = MethodLibrary()
    private var _binding: FragmentUpdateClassWithSubjectBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: getSubjectViewModel
    private lateinit var subjectList: List<Subject>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateClassWithSubjectBinding.inflate(inflater, container, false)

        // Retrieve data passed via the bundle and populate the fields
        binding.subjectName.setText(arguments?.getString("subject_name") ?: "")
        binding.classes.setText(arguments?.getString("class_name") ?: "")
        binding.classes2.setText(arguments?.getString("class_id") ?: "")
        binding.marks.setText(arguments?.getString("marks") ?: "")
        binding.Etid.setText(arguments?.getString("ClassId") ?: "")

        // Initialize ViewModel and fetch data
        val apiService = RetrofitHelper.getApiService()
        val repository = getSubjectRepository(apiService)
        val factory = getSubjectViewModelFactory(repository, SchoolId().getSchoolId(requireContext()))
        viewModel = ViewModelProvider(this, factory).get(getSubjectViewModel::class.java)

        // Observe the subjects data and update UI
        observeSubjects()

        // Set up submit button to update the subject
        binding.submitButton.setOnClickListener {
            if (!tooBox.isInternetAvailable(requireContext())){
                tooBox.deviceOffLineMassage(requireContext())
            }else{
                val subjectNameInput = binding.subjectName.text.toString().trim()
                val classesInput = binding.classes.text.toString().trim()
                val marksInput = binding.marks.text.toString().trim()
                val classId = binding.Etid.text.toString().trim()

                // Check if any fields are empty
                if (subjectNameInput.isNotEmpty() && classesInput.isNotEmpty() && marksInput.isNotEmpty()) {
                    updateSubject(subjectNameInput, classesInput, marksInput, classId)
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }



        }

        // Set up delete button to delete the subject
        binding.deleteButton.setOnClickListener {
            if (!tooBox.isInternetAvailable(requireContext())){
                tooBox.deviceOffLineMassage(requireContext())
            }else{
                val selectedSubject = binding.SelectClass.selectedItem as String
                tooBox.showConfirmationDialog(
                    context = requireContext(), // or requireContext() if in a Fragment
                    title = "Confirm Deletion",
                    message = "Are you sure you want to delete this subject : ${selectedSubject}",
                    positiveButtonText = "Yes",
                    positiveButtonAction = {
                        deleteSubject(binding.Etid.text.toString()) },
                    negativeButtonText = "No",
                    negativeButtonAction = {  },
                    cancelable = false )
            }
        }

        return binding.root
    }

    // Observing the subjects list to populate the Spinner based on class name
    private fun observeSubjects() {
        viewModel.classWithSubjects.observe(viewLifecycleOwner) { classWithSubjects ->
            // Filter subjects based on the class name
            val className = arguments?.getString("class_name") ?: ""
            val filteredSubjects = classWithSubjects
                .filter { it.classes == className }
                .flatMap { it.subjects }

            if (filteredSubjects.isNotEmpty()) {
                subjectList = filteredSubjects
                setupSpinner(subjectList)
            } else {
                Toast.makeText(requireContext(), "No subjects found for class $className", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Setup the Spinner with subject names and their IDs
    private fun setupSpinner(subjects: List<Subject>) {
        val subjectNames = subjects.map { "${it.subject_name} (ID: ${it.id}) (Marks: ${it.marks})" }
        val subjectName = subjects.map { it.subject_name }

        // Create an ArrayAdapter using the subject names
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter to the Spinner
        binding.SelectClass.adapter = adapter

        // Set default value for the spinner (if needed)
        val selectedSubjectName = arguments?.getString("subject_name") ?: ""
        val selectedIndex = subjectNames.indexOfFirst { it.contains(selectedSubjectName) }
        if (selectedIndex != -1) {
            binding.SelectClass.setSelection(selectedIndex)
        }

        // Set OnItemSelectedListener to update the EditText fields when an item is selected
        binding.SelectClass.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSubject = subjects[position]
                binding.subjectName.setText(selectedSubject.subject_name)
                binding.Etid.setText(selectedSubject.id.toString())
                binding.marks.setText(selectedSubject.marks.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })
    }

    // Update the subject details based on user input
    private fun updateSubject(subjectName: String, classes: String, marks: String, classId: String) {
        val subjectDataMap = mapOf(
            "classes" to classes,
            "subject_name" to subjectName,
            "marks" to marks,
            "school_id" to SchoolId().getSchoolId(requireContext()),
            "id" to classId
        )

        RetrofitHelper.getApiService().updateSubject(subjectDataMap)
            .enqueue(object : Callback<ClassWithResposne> {
                override fun onResponse(call: Call<ClassWithResposne>, response: Response<ClassWithResposne>) {
                    if (response.isSuccessful) {
                        tooBox.showConfirmationDialog(
                            context = requireContext(), // or requireContext() if in a Fragment
                            title = "Subject Updated Successfully",
                            message = "Would you like to more subject?",
                            positiveButtonText = "YES",
                            positiveButtonAction = {  },
                            negativeButtonText = "NO",
                            negativeButtonAction = { tooBox.fragmentChanger(ClassWithSubject(), requireContext()) },
                            cancelable = false)
                    } else {
                        Toast.makeText(requireContext(), "Failed to update subject", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ClassWithResposne>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Function to delete a subject
    private fun deleteSubject(subjectId: String) {
        RetrofitHelper.getApiService().deleteSubject(SchoolId().getSchoolId(requireContext()), subjectId)
            .enqueue(object : Callback<DeleteSubjectResponse> {
                override fun onResponse(call: Call<DeleteSubjectResponse>, response: Response<DeleteSubjectResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                        tooBox.fragmentChanger(ClassWithSubject(), requireActivity())
//                        tooBox.showConfirmationDialog(
//                            context = requireContext(),
//                            title = "Subject Deleted Successfully",
//                            positiveButtonText = "EDIT",
//                            positiveButtonAction = { observeSubjects() },
//                            negativeButtonText = "SUBJECT",
//                            negativeButtonAction = {
//                                tooBox.fragmentChanger(ClassWithSubject(), requireActivity())
//                                                   },
//                            cancelable = true )
//                        navigateToClassWithSubjectFragment()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DeleteSubjectResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Clean up the binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


