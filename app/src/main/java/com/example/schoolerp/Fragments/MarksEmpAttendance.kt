package com.example.schoolerp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.AddEmpAttandanceAdapter
import com.example.schoolerp.Adapter.AttendanceForClickableStatus
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.EmployeeAttendance
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentMarksEmpAttendanceBinding
import com.example.schoolerp.repository.AllEmployeesRepository
import com.example.schoolerp.repository.EmloyeesAttendanceRepository
import com.example.schoolerp.repository.EmployeeAttendanceRepository
import com.example.schoolerp.viewmodel.AllEmployeesViewModel
import com.example.schoolerp.viewmodel.EmployeeAttendanceViewModel
import com.example.schoolerp.viewmodel.EmployeesAttendanceViewModel
import com.example.schoolerp.viewmodelfactory.AllEmployeesViewModelFactory
import com.example.schoolerp.viewmodelfactory.EmployeeAttendanceViewModelFactory
import com.example.schoolerp.viewmodelfactory.EmployeesAttendanceViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MarksEmpAttendance : Fragment() {

    private lateinit var binding: FragmentMarksEmpAttendanceBinding
    private lateinit var viewModelAllEmp: AllEmployeesViewModel
    private lateinit var adapter: AddEmpAttandanceAdapter     // Adapter for adding employee attendance
    private lateinit var adapter1: AttendanceForClickableStatus // Adapter for displaying attendance status

    private lateinit var attendanceViewModel: EmployeeAttendanceViewModel
    private val attendanceMap = mutableMapOf<String, String>()
    private val toolBox = MethodLibrary()
    private lateinit var viewModelAttendanceEmployees: EmployeesAttendanceViewModel
    private val existingEmployees = mutableSetOf<String>() // Store existing employee names

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarksEmpAttendanceBinding.inflate(inflater, container, false)

        setupViewModel()
        setupViewModelAddEmp()
        setupRecyclerView(mutableListOf())  // Initialize with empty list
        btnEmpSetUpListeners()

        setupViewModelAttendanceEmployee()

        return binding.root
    }

    private fun setupRecyclerView(allEmployees: List<EmployeeAttendance>) {
        adapter1 = AttendanceForClickableStatus(allEmployees) { employeeName, status ->
            attendanceMap[employeeName] = status
            Log.d("AttendanceMap", "Updated attendance for $employeeName: $status")
        }

        binding.EmpRecylerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapter1
        }
    }

    private fun setupViewModelAttendanceEmployee() {
        val apiService = RetrofitHelper.getApiService()
        val repository = EmloyeesAttendanceRepository(apiService)
        val factory = EmployeesAttendanceViewModelFactory(repository)
        viewModelAttendanceEmployees = ViewModelProvider(this, factory).get(EmployeesAttendanceViewModel::class.java)

        val schoolId = getSchoolId()
        viewModelAttendanceEmployees.getEmployeeAttendance(schoolId.trim())
        observeEmployeesAttendanceData() // Observe attendance data
    }

    private fun observeEmployeesAttendanceData() {
        viewModelAttendanceEmployees.employeeAttendanceData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null && response.status) {
                    val allEmployees = response.data.reversed() // Reverse the list without filtering by date

                    if (allEmployees.isNotEmpty()) {
                        // If attendance data is available, show it
                        setupRecyclerView(allEmployees)
                        Log.d("AttendanceData", "Showing attendance: ${allEmployees.size} records")
                    } else {
                        // No attendance data found, fallback to employee list
                        Log.d("AttendanceData", "No attendance data found, fetching employee list.")
                        fetchEmployees()
                        observeViewModel()
                    }
                } else {
                    Log.e("AttendanceError", "Failed to retrieve attendance data. Falling back to employee list.")
                    fetchEmployees()
                    observeViewModel()
                }
            }
        )
    }


    private fun fetchEmployees() {
        val schoolId = getSchoolId()
        viewModelAllEmp.getAllEmployees(schoolId.trim())
    }

    private fun setupViewModel() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AllEmployeesRepository(apiService)
        val factory = AllEmployeesViewModelFactory(repository)
        viewModelAllEmp = ViewModelProvider(this, factory).get(AllEmployeesViewModel::class.java)
    }

    private fun setupViewModelAddEmp() {
        val apiService = RetrofitHelper.getApiService()
        val repository = EmployeeAttendanceRepository(apiService)
        val factory = EmployeeAttendanceViewModelFactory(repository)
        attendanceViewModel = ViewModelProvider(this, factory).get(EmployeeAttendanceViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModelAllEmp.employeeResponse.observe(viewLifecycleOwner) { response ->
            val employees = response?.employee?.toMutableList() ?: mutableListOf()
            Log.d("EmployeesData", "Employees retrieved: ${employees.size}")
            if (employees.isNotEmpty()) {
                adapter = AddEmpAttandanceAdapter(employees) { employeeName, status ->
                    attendanceMap[employeeName] = status // Capture selections for submission
                }
                binding.EmpRecylerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = this@MarksEmpAttendance.adapter // Use the first adapter for adding employees
                }
            } else {
                showAddEmployeesDialog()
            }
        }
    }



    private fun btnEmpSetUpListeners() {
        binding.btnSubmit.setOnClickListener {
            submitAttendance()
            submitAttendance11()

        }
    }
    private fun submitAttendance() {
        val attendanceList = mutableListOf<Map<String, String>>()

        // Ensure that adapter is initialized before using it
        if (::adapter.isInitialized) {
            attendanceMap.forEach { (employeeName, status) ->
                val employee = adapter.getEmployees().find { it.employee_name == employeeName }
                val employeeId = employee?.id ?: ""

                val attendanceData = mapOf(
                    "employee_name" to employeeName,
                    "status" to status,
                    "date" to getCurrentDate(),
                    "school_id" to getSchoolId(),
                    "employee_id" to employeeId
                )
                attendanceList.add(attendanceData)
                Log.d("Attendance", "Added attendance data for employee $employeeName")
            }

            // Default all employees who were not marked with status to "P"
            adapter.getEmployeeNames().forEach { employeeName ->
                if (!attendanceMap.contains(employeeName)) {
                    val employee = adapter.getEmployees().find { it.employee_name == employeeName }
                    val employeeId = employee?.id ?: ""

                    val attendanceData = mapOf(
                        "employee_name" to employeeName,
                        "status" to "P", // Default to Present
                        "date" to getCurrentDate(),
                        "school_id" to getSchoolId(),
                        "employee_id" to employeeId
                    )
                    attendanceList.add(attendanceData)
                }
            }

            if (attendanceList.isNotEmpty()) {
                attendanceList.forEach { attendanceData ->
                    Log.d("AttendanceData", "Submitting attendance: $attendanceData")
                    attendanceViewModel.addEmployeeAttendance(attendanceData)
                }

                observeAttendanceResponse() // Observe response after submission

                Toast.makeText(requireContext(), "Attendance submitted for ${attendanceList.size} employees.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No employees to submit attendance for", Toast.LENGTH_SHORT).show()
            }
        } else {
            //Log.e("AdapterError", "Adapter has not been initialized.")
           // Toast.makeText(requireContext(), "Error: Adapter not initialized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitAttendance11() {
        val attendanceList = mutableListOf<Map<String, String>>()

        // Ensure that adapter1 is initialized before using it
        if (::adapter1.isInitialized) {
            adapter1.getEmployees().forEach { employee ->
                val employeeId = employee.employee_id ?: ""

                // Collect attendance data for each employee
                val attendanceData = mapOf(
                    "employee_name" to employee.employee_name,
                    "status" to employee.status,
                    "date" to getCurrentDate(),
                    "school_id" to getSchoolId(),
                    "employee_id" to employeeId
                )
                attendanceList.add(attendanceData)
                Log.d("Attendance", "Added attendance data for employee ${employee.employee_name}")
            }

            // Ensure all employees who were not marked with a status are set to "P" (Present)
            adapter1.getEmployeeNames().forEach { employeeName ->
                if (!attendanceList.any { it["employee_name"] == employeeName }) {
                    val employee = adapter1.getEmployees().find { it.employee_name == employeeName }
                    val employeeId = employee?.employee_id ?: ""

                    val attendanceData = mapOf(
                        "employee_name" to employeeName,
                        "status" to "P", // Default to Present
                        "date" to getCurrentDate(),
                        "school_id" to getSchoolId(),
                        "employee_id" to employeeId
                    )
                    attendanceList.add(attendanceData)
                }
            }

            // Submit the collected attendance data
            if (attendanceList.isNotEmpty()) {
                attendanceList.forEach { attendanceData ->
                    Log.d("AttendanceData", "Submitting attendance: $attendanceData")
                    attendanceViewModel.addEmployeeAttendance(attendanceData)
                }

                observeAttendanceResponse() // Observe response after submission

                Toast.makeText(requireContext(), "Attendance submitted for ${attendanceList.size} employees.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No employees to submit attendance for", Toast.LENGTH_SHORT).show()
            }
        } else {
           // Log.e("AdapterError", "Adapter1 has not been initialized.")
           // Toast.makeText(requireContext(), "Error: Adapter1 not initialized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault() // Ensuring the time zone is set to the device's default
        return sdf.format(calendar.time)
    }

    private fun getSchoolId(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("onboarding_prefs", 0)
        return sharedPreferences.getString("school_id", "defaultSchoolId") ?: "defaultSchoolId"
    }

    private fun showAddEmployeesDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "No Employees are available.",
            message = "Would you like to add Employees now?",
            positiveButtonText = "YES",
            positiveButtonAction = { toolBox.fragmentChanger(AddNewEmployees(), requireContext()) },
            negativeButtonText = "NO",
            negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
    }

    private fun observeAttendanceResponse() {
        attendanceViewModel.attendanceResponse.observe(viewLifecycleOwner) { response ->
            if (response.status) {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}