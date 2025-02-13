package com.example.schoolerp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.Fragments.Fragment.AddStudent
import com.example.schoolerp.R
import com.example.schoolerp.adapter.StudentNamesAdapter
import com.example.schoolerp.databinding.FragmentMarksStudentAttendanceBinding
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.DateWiseStudentAttendanceRepository
import com.example.schoolerp.repository.StudentAttendanceRepository
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.DateWiseStudentAttendanceViewModel
import com.example.schoolerp.viewmodel.StudentAttendanceViewModel
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.DateWiseStudentAttendanceViewModelFactory
import com.example.schoolerp.viewmodelfactory.StudentAttendanceViewModelFactory
import java.util.Calendar

class MarksStudentAttendance : Fragment() {
    private lateinit var binding: FragmentMarksStudentAttendanceBinding
    private lateinit var viewmodelAllClass: AllClassViewModel
    private lateinit var viewmodelDateWise: DateWiseStudentAttendanceViewModel
    private lateinit var viewmodelAddStudentAttandance: StudentAttendanceViewModel

    val toolBox = MethodLibrary()

    private var selectedDate: String = getCurrentDate()
    private var selectedClassName: String? = null
    private var classNameList: List<String> =
        emptyList()
    private lateinit var studentNamesAdapter: StudentNamesAdapter
    private lateinit var viewModel: StudentAttendanceViewModel
    private val attendanceMap = mutableMapOf<String, String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarksStudentAttendanceBinding.bind(
            inflater.inflate(R.layout.fragment_marks_student_attendance, null)
        )
        setupViewModelAllClass()
        setupViewModelClassNameStudent()
        // setupListeners()
        observeDataAllClass()
        setupListenerss() // Ensure listeners are set up correctly
        observeDataAllName() // Start observing student data
        setupViewModel()
        setupSubmitButton()
        return binding.root
    }

    private fun setupViewModelClassNameStudent() {
        val repository = AllClassRepository()
        val factory = AllClassViewModelFactory(repository)
        viewmodelAllClass = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)
    }

    private fun getSchoolId(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences(
            "onboarding_prefs",
            AppCompatActivity.MODE_PRIVATE
        )
        val schoolId = sharedPreferences.getString("school_id", null)
        if (schoolId != null) {
            Log.d("MarkStudentAttendance", "School ID: $schoolId")
        } else {
            Log.d("MarkStudentAttendance", "School ID not found in SharedPreferences")
        }
        return schoolId
    }

    private fun setupViewModelAllClass() {
        val apiService = RetrofitHelper.getApiService()
        val repository = DateWiseStudentAttendanceRepository(apiService)
        val factory = DateWiseStudentAttendanceViewModelFactory(repository)
        viewmodelDateWise =
            ViewModelProvider(this, factory).get(DateWiseStudentAttendanceViewModel::class.java)
    }

    private fun observeDataAllClass() {
        val schoolId = getSchoolId() ?: run {
            Log.e("MarksStudentAttendance", "School ID is null or blank. Cannot fetch data.")
            return
        }

        viewmodelAllClass.getClasses(schoolId.trim())
            .observe(viewLifecycleOwner, Observer { response ->
                if (response != null && response.status) {
                    Log.d("MarkStudentAttendance", "Classes fetched successfully: ${response.data}")

                    // Extracting class names and class IDs
                    val classNames = response.data.map { it.class_name }
                    val classIds = response.data.map { it.class_id }

                    setupSpinner(classNames, classIds)  // Passing both class names and class IDs
                } else {
                    Log.e("MarkStudentAttendance", "Failed to fetch class data or no data available.")
                    showAddClassDialog()
                }
            })
    }

    private fun setupSpinner(classNames: List<String>, classIds: List<String>) {
        if (classNames.isEmpty()) {
            Log.d("MarkStudentAttendance", "No classes found.")
            binding.txtClassName.isEnabled = false
            return
        }

        // Setting up adapter for the first spinner (class names)
        val classNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classNames)
        classNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.txtClassName.adapter = classNameAdapter

        // Handling item selection in the first spinner (class names)
        binding.txtClassName.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedClassName = classIds[position]
                    Log.d("MarkStudentAttendance", "Selected class: $selectedClassName")
                    // Fetch attendance data for the selected class
                    fetchAttendanceData()
                    // Get the selected class name from the first spinner
                    val selectedClassName = parentView.getItemAtPosition(position) as String

                    // Find the index of the selected class name
                    val selectedIndex = classNames.indexOf(selectedClassName)

                    // Ensure the selectedIndex is valid
                    if (selectedIndex >= 0) {
                        // Get the corresponding class ID for the selected class name
                        val selectedClassId = classIds[selectedIndex]

                        // Update the second spinner (txtonlyId) with the corresponding class ID
                        val updatedAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            listOf(selectedClassId)
                        )
                        updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.txtonlyId.adapter = updatedAdapter
                    } else {
                        // If no matching class ID, set default "Select Class ID"
                        binding.txtonlyId.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            listOf("Select Class ID")
                        )
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    // Handle case when nothing is selected (optional)
                    // Set the default "Select Class ID" in the second spinner
                    binding.txtonlyId.adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        listOf("Select Class ID")
                    )
                }
            }
    }
    private fun showAddClassDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Students Not Available",
            message = "You need to add Students first",
            positiveButtonText = "ADD Students",
            positiveButtonAction = { toolBox.fragmentChanger(AddStudent(), requireContext()) },
            negativeButtonText = "",
            negativeButtonAction = { },
            cancelable = false
        )
    }

    private fun navigateToAddClassScreen() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, NewClass())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setupViewModel() {
        val apiService = RetrofitHelper.getApiService()
        val repository = StudentAttendanceRepository(apiService)
        val factory = StudentAttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(StudentAttendanceViewModel::class.java)
    }

    private fun setupSubmitButton() {
        binding.submitAttendanceButton.setOnClickListener {
            // Collect only the modified attendance data
            val attendanceList = mutableListOf<Map<String, String>>()

            studentNamesAdapter.studentNames.forEach { student ->
                val status = attendanceMap[student.id] ?: "A" // Use the map value, or default to "A" if not updated

                val attendanceData = mapOf(
                    "st_name" to student.st_name,
                    "status" to status,
                    "date" to selectedDate,
                    "school_id" to getSchoolId().orEmpty(),
                    "class" to selectedClassName.orEmpty(),
                    "student_id" to student.id
                )
                attendanceList.add(attendanceData)
            }

            // Submit only modified attendance for each student
            submitAttendanceForStudents(attendanceList)
            Toast.makeText(requireContext(), "Attendance successfully submitted.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun submitAttendanceForStudents(attendanceList: List<Map<String, String>>) {
        // Submit each student's attendance data
        attendanceList.forEach { attendanceData ->
            viewModel.addStudentAttendance(attendanceData)
        }
    }


    private fun observeDataAllName() {
        // Observe attendance data from ViewModel
        // Inside your observer, when you first receive student data (in observeDataAllName())
        viewmodelDateWise.attendanceData.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.status && !response.data.students.isNullOrEmpty()) {
                val studentNames = response.data.students
                toggleEmptyView(false) // Show RecyclerView, hide default text
                setupRecyclerView(studentNames) // Populate RecyclerView with data

                // Initialize attendanceMap with current attendance status
                studentNames.forEach { student ->
                    // Initialize the attendance map with the student's existing attendance status
                    attendanceMap[student.id] = student.attendance_status ?: "A" // Default to "A" if not set
                }
            }
        })
    }

    private fun toggleEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.visibility = View.VISIBLE
            binding.submitAttendanceButton.visibility=View.GONE
            binding.recyclerViewStudentNames.visibility = View.GONE
        } else {
            binding.txtForDefault.visibility = View.GONE
            binding.recyclerViewStudentNames.visibility = View.VISIBLE
            binding.submitAttendanceButton.visibility=View.VISIBLE
        }

        setuplistner(isEmpty)
    }

    private fun setuplistner(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.setOnClickListener {
                // Navigate to AddStudentFragment when "No data" text is clicked
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, AddStudent()) // Replace with your container ID
                transaction.addToBackStack(null) // Add transaction to back stack if you want to navigate back
                transaction.commit()
            }
        } else {
            binding.txtForDefault.setOnClickListener(null) // Remove the click listener if data is not empty
        }
    }

    private fun setupRecyclerView(studentNames: List<Student>) {
        studentNamesAdapter = StudentNamesAdapter(studentNames) { studentID, status ->
            // Update the attendance map with the new status for the student
            attendanceMap[studentID] = status
            // Log the updated attendance status for debugging
            Log.d("MarkStudentAttendance", "Updated attendance: $studentID -> $status")
        }

        // Set up the RecyclerView
        binding.recyclerViewStudentNames.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = studentNamesAdapter
        }
    }

    private fun fetchAttendanceData() {
        val schoolId = getSchoolId()?.trim()
        val classId = selectedClassName?.trim()

        if (!schoolId.isNullOrEmpty() && !classId.isNullOrEmpty()) {
            viewmodelDateWise.fetchAttendance(schoolId, classId)
            Log.d(
                "MarkStudentAttendance",
                "Fetching attendance for school ID: $schoolId, class: $classId"
            )
        } else {
            Log.e(
                "MarkStudentAttendance",
                "Cannot fetch attendance. Missing or invalid school ID or class name."
            )
        }
    }

    private fun displayStudentNames(classNameList: List<String>) {
        // Check if the list is not empty
        if (classNameList.isNotEmpty()) {
            // Convert the list of student names into a comma-separated string
            val studentNames = classNameList.joinToString(", ")
            // Set the text of the TextView with the student names
            binding.classNameText.text = studentNames
        } else {
            // If no student names are available, display a default message
            binding.classNameText.text = "No students available"
        }
    }

    private fun setupListenerss() {
        binding.btnSearch.setOnClickListener {
            if (!selectedClassName.isNullOrEmpty() && classNameList.isNotEmpty()) {
                // Get the selected date (replace this with the actual selected date)
                // val selectedDate = "2024-11-16"  // Replace with your actual selected date

                // Convert the list of student names into a comma-separated string
                val studentNames = classNameList.joinToString(", ")

                // Navigate to MarksStudentAttendanceAllClassStudent with the selected class, date, and student names
                val fragment = MarksStudentAttendanceAllClassStudent()
                val bundle = Bundle()
                bundle.putString("className", selectedClassName)
                bundle.putString("selectedDate", selectedDate)
                bundle.putString(
                    "studentNames",
                    studentNames
                )  // Pass the student names to the fragment
                fragment.arguments = bundle

                // Use FragmentTransaction to replace the current fragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = String.format("%04d-%02d-%02d", year, month, day)

        // Add Log.d to print the current date
        Log.d("CurrentDate", "The current date is: $currentDate")

        return currentDate
    }
}
