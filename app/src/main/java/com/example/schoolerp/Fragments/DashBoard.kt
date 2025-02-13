package com.example.schoolerp.Fragments

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.helpers.MethodLibrary
import com.example.principle.PrincipalDetails
import com.example.schoolerp.Adapter.AdmetionAdapter
import com.example.schoolerp.Adapter.BirthDayEmpAdapter
import com.example.schoolerp.Adapter.BirthdayStudentAdapter
import com.example.schoolerp.Adapter.EmployeeAttandanceAdapter
import com.example.schoolerp.Adapters.AbsentStudentAdapter
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AbsentStudent
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.DataClasses.CalendarEvent
import com.example.schoolerp.DataClasses.ClassItem
import com.example.schoolerp.DataClasses.Employee
import com.example.schoolerp.DataClasses.EmployeeAttendance
import com.example.schoolerp.DataClasses.Homework
import com.example.schoolerp.DataClasses.OverViewData
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.Fragments.Fragment.AddStudent
import com.example.schoolerp.ImageUtil.ImageUtil
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentDashBoardBinding
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.AllEmployeesRepository
import com.example.schoolerp.repository.EmloyeesAttendanceRepository
import com.example.schoolerp.repository.GetAttendanceRepository
import com.example.schoolerp.repository.GetCalenderRepository
import com.example.schoolerp.repository.GetCollectionStatusRepository
import com.example.schoolerp.repository.GetHomeworkRepository
import com.example.schoolerp.repository.GetRevenueRepository
import com.example.schoolerp.repository.GetTotalProfitRepository
import com.example.schoolerp.repository.getOverviewRepository
import com.example.schoolerp.repository.getStudentRepository
import com.example.schoolerp.util.CircularProgressViewAbsent
import com.example.schoolerp.util.CircularProgressViewLeave
import com.example.schoolerp.util.CircularProgressViewPresent
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.AllEmployeesViewModel
import com.example.schoolerp.viewmodel.AttendanceViewModel
import com.example.schoolerp.viewmodel.EmployeesAttendanceViewModel
import com.example.schoolerp.viewmodel.EmployeesPresentlyViewModel
import com.example.schoolerp.viewmodel.GetCalenderViewModel
import com.example.schoolerp.viewmodel.GetCollectionStatusViewModel
import com.example.schoolerp.viewmodel.GetHomeworkViewModel
import com.example.schoolerp.viewmodel.GetRevenueViewModel
import com.example.schoolerp.viewmodel.GetTotalProfitViewModel
import com.example.schoolerp.viewmodel.StudentPresentlyViewModel
import com.example.schoolerp.viewmodel.employeeCheckStatusViewModel
import com.example.schoolerp.viewmodel.getOverViewModel
import com.example.schoolerp.viewmodel.getStudentViewModel
import com.example.schoolerp.viewmodel.studentCheckStatusViewModel
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.AllEmployeesViewModelFactory
import com.example.schoolerp.viewmodelfactory.AttendanceViewModelFactory
import com.example.schoolerp.viewmodelfactory.EmployeesAttendanceViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetCalenderViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetCollectionStatusVIewModelFactory
import com.example.schoolerp.viewmodelfactory.GetHomeworkViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetProfitViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetRevenueViewModelFactory
import com.example.schoolerp.viewmodelfactory.StudentPresentlyRepository
import com.example.schoolerp.viewmodelfactory.StudentPresentlyViewModelFactory
import com.example.schoolerp.viewmodelfactory.getStudentViewModelFactory
import com.example.schoolerp.viewmodelfactory.getoverViewModelFactory
import com.example.student.StudentInfo
import com.example.teacher.TeacherDetails
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.datepicker.DayViewDecorator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import java.util.*


class DashBoard : Fragment() {
    private lateinit var binding: FragmentDashBoardBinding
    private lateinit var viewmodelOverview: getOverViewModel
    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var getHomeworkData: List<Homework>
    private lateinit var viewModelGetHomework: GetHomeworkViewModel
    private lateinit var lineChartEstimationRemaining: HorizontalBarChart
    private lateinit var horizontalBarChart: BarChart
    private lateinit var horizontalBarChartPresently: HorizontalBarChart
    private lateinit var absentStudentAdapter: AbsentStudentAdapter
    private var absentStudents = mutableListOf<AbsentStudent>()
    private lateinit var viewModelAllStudent: getStudentViewModel
    private val toolbox = MethodLibrary()
    private val employeesPresentlyViewModel: EmployeesPresentlyViewModel by viewModels()
    private lateinit var BirthdayStudentadapter: BirthdayStudentAdapter
    private var Birthdaystudent = mutableListOf<Student>()
    private lateinit var viewmodelcalender: GetCalenderViewModel

    private lateinit var absentEmployeesAdapter: EmployeeAttandanceAdapter
    private lateinit var viewModel: StudentPresentlyViewModel
    private var StudentImage: String = "assetsNew/img/student/"
    private var PrincipalImage: String = "assetsNew/img/employee/"
    private var InstituteLogo: String = "assetsNew/img/institute_logos/"
    var stdetails = StudentInfo()
    var teacherDetails = TeacherDetails()
    var isExpanded: Boolean = false
    //progressviewdeclaration for student dashboard to display student attendance percentage

    private lateinit var circularProgressViewPresent: CircularProgressViewPresent
    private lateinit var circularProgressViewAbsent: CircularProgressViewAbsent
    private lateinit var circularProgressViewLeave: CircularProgressViewLeave

    private lateinit var circularProgressViewPresentPricipal: CircularProgressViewPresent
    private lateinit var circularProgressViewAbsentPricipal: CircularProgressViewAbsent
    private lateinit var circularProgressViewLeavePricipal: CircularProgressViewLeave

    private var absentEmployees = mutableListOf<EmployeeAttendance>()

    private lateinit var viewModelAllEmployees: AllEmployeesViewModel
    private lateinit var viewModelAllAttendance: AttendanceViewModel
    private lateinit var viewModelAttendanceEmployees: EmployeesAttendanceViewModel
    private lateinit var viewModelAllClasses: AllClassViewModel
    private val estimationEntries = ArrayList<BarEntry>()
    private val remainingEntries = ArrayList<BarEntry>()
    private val plannedEntries = ArrayList<BarEntry>()

    private lateinit var ViewmodelReveneues: GetRevenueViewModel
    private lateinit var ViewmodelProfit: GetTotalProfitViewModel
    private lateinit var ViewmodelCollectionStatus: GetCollectionStatusViewModel
    private var allemplist = mutableListOf<AllEmployee>()
    private lateinit var empbirthdayadapter: BirthDayEmpAdapter

    private var addmissionList = mutableListOf<Student>()
    private lateinit var addmissionadapter: AdmetionAdapter
    private val holidayDates = HashSet<Long>()
    private val eventDates = HashSet<Long>()

    private lateinit var eventList: List<CalendarEvent>

    private val studentViewModel : studentCheckStatusViewModel by viewModels()
    private val employeeViewModel : employeeCheckStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentDashBoardBinding.bind(inflater.inflate(R.layout.fragment_dash_board, null))


        lineChart = binding.lineChartAccountOverview
        barChart = binding.pieChartClassStrength
        // lineChartEstimationRemaining = binding.horizontalBarChartFees
        horizontalBarChart = binding.horizontalBarChart
        horizontalBarChartPresently = binding.horizontalBarChartPresently
        CheckRole()
        loadDashBoardCircle()

        binding.showmypersonalinfo.setOnClickListener {
            animationLinearLayout(
                binding.layoutstudentpersonalinfo,
                0,
                350,
                500
            )
        }
        binding.showmypersonalinfoprincipal.setOnClickListener {
            animationLinearLayout(
                binding.layoutprincipalpersonalinfo,
                0,
                560,
                15005
            )
        }

        return binding.root
    }

    private fun getSchoolId(): String {
        // Retrieve the school_id from shared preferences
        val sharedPreferences = requireActivity().getSharedPreferences(
            "onboarding_prefs",
            AppCompatActivity.MODE_PRIVATE
        )
        val schoolId = sharedPreferences.getString("school_id", null)

        if (schoolId != null) {
            Log.d("AddNewEmployees", "School ID retrieved from SharedPreferences: $schoolId")
        } else {
            Log.d("AddNewEmployees", "School ID not found in SharedPreferences")
        }

        return schoolId ?: "defaultSchoolId"
    }

    private fun setupViewModelProfit() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetTotalProfitRepository(apiService)
        val factory = GetProfitViewModelFactory(repository)
        ViewmodelProfit = ViewModelProvider(this, factory).get(GetTotalProfitViewModel::class.java)
        val schoolId = getSchoolId()
        ViewmodelProfit.fetchProfit(schoolId.trim())

    }

    private fun observalProfit() {
        ViewmodelProfit.profit.observe(viewLifecycleOwner, Observer { response ->
            Log.d("profitData", "Raw response: $response")

            response?.let {
                Log.d("profitData", "Data status: ${it.status}")
                val profitData = it.data
                if (profitData != null) {
                    // Log.d("profitData", "Grand Total: ${profitData.grand_total}")
                    //  Log.d("profitData", "Month Total: ${profitData.month_total}")

                    binding.TotolProfit.text = "₹ ${profitData.grand_total}"
                    binding.thisprofit.text = "₹ ${profitData.month_total}"
                } else {
                    Log.d("profitData", "profitData is null")
                    binding.TotolProfit.text = "₹00"
                    binding.thisprofit.text = "₹00"
                }
            } ?: run {
                Log.d("profitData", "Response is null")
                binding.TotolProfit.text = "₹00"
                binding.thisprofit.text = "₹00"
            }
        })
    }

    private fun setupViewModelRevenuest() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetRevenueRepository(apiService)
        val factory = GetRevenueViewModelFactory(repository)
        ViewmodelReveneues = ViewModelProvider(this, factory).get(GetRevenueViewModel::class.java)
        val schoolId = getSchoolId()
        if (schoolId.isNotBlank()) {
            //  Log.d("RevenueData", "Fetching revenue for school ID: $schoolId")
            ViewmodelReveneues.fetchRevenue(schoolId.trim())
        } else {
            binding.TotalRevenues.text = "₹00"
        }
    }

    private fun fetchRevenue() {
        val schoolId = getSchoolId()
        if (schoolId.isNotBlank()) {
            ViewmodelReveneues.fetchRevenue(schoolId.trim())
        } else {
            //  Log.d("RevenueData", "School ID is blank!")
            binding.TotalRevenues.text = "₹00"
        }
    }

    private fun observalRevenues() {
        // Observe the LiveData object for changes
        ViewmodelReveneues.revenue.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                Log.d("RevenueData", "Data status: ${it.status}")
                val revenueList = it.data
                if (!revenueList.isNullOrEmpty()) {
                    val revenueData = revenueList[0] // Assuming you need the first item
                    Log.d("RevenueData", "Revenue total: ${revenueData.revenue_total}")
                    binding.TotalRevenues.text = "₹ ${revenueData.revenue_total}"
                    binding.ThisMonthTotal.text = "₹ ${revenueData.month_total}"
                } else {
                    Log.d("RevenueData", "Data list is empty")
                    binding.TotalRevenues.text = "₹00"
                    binding.ThisMonthTotal.text = "₹00"
                }
            } ?: run {
                Log.d("RevenueData", "Response is null")
                binding.TotalRevenues.text = "₹00"
                binding.ThisMonthTotal.text = "₹00"
            }
        })
    }

    private fun setupViewModelAllStudent() {
        val apiService = RetrofitHelper.getApiService()
        val repository = getStudentRepository(apiService)
        val factory = getStudentViewModelFactory(repository)
        viewModelAllStudent = ViewModelProvider(this, factory).get(getStudentViewModel::class.java)

    }

    private fun fetchStudent() {
        val schoolId = getSchoolId()
        viewModelAllStudent.fetchStudentsBySchoolId(schoolId.trim())
    }

    private fun observeStudents() {
        viewModelAllStudent.students.observe(viewLifecycleOwner) { students ->
            val currentMonthStudents = getCurrentMonthStudents(students)
            val studentCount = students.size

            binding.studentCountText.text = studentCount.toString()
            binding.txtTotalStudent.text = studentCount.toString()

            val currentMonthStudentCount = currentMonthStudents.size
            binding.currentMonthStudentCountText.text = currentMonthStudentCount.toString()

            // Update the adapter with the current month students
            addmissionadapter.updateadmationData(currentMonthStudents)
            toggleEmptyViewCurrentMonth(currentMonthStudentCount == 0)
            Log.d("DashBoard", "Total student count observed: $studentCount")
            Log.d("DashBoard", "Current month student count observed: $currentMonthStudentCount")
        }
    }

    private fun getCurrentMonthStudents(students: List<Student>): List<Student> {
        val currentMonth = java.util.Calendar.getInstance()
            .get(java.util.Calendar.MONTH) + 1 // Months are 0-indexed
        val dateFormat = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ) // Adjusted format to match "dd/MM/yyyy"

        return students.filter { student ->
            student.dt_of_admission?.let { admissionDateString ->
                try {
                    val admissionDate = dateFormat.parse(admissionDateString)
                    val calendar = java.util.Calendar.getInstance().apply {
                        time = admissionDate
                    }
                    val admissionMonth = calendar.get(java.util.Calendar.MONTH) + 1
                    admissionMonth == currentMonth
                } catch (e: ParseException) {

                    Log.e("DashBoard", "Date parsing failed for $admissionDateString", e)
                    false
                }
            } ?: false
        }
    }

    private fun initViewAdmetion() {
        // Initialize RecyclerViews with GridLayoutManager
        binding.RecyclerAdmetion.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)

        // Initialize the adapter with an empty list initially
        addmissionadapter = AdmetionAdapter(emptyList())

        // Set the adapter to the RecyclerView
        binding.RecyclerAdmetion.adapter = addmissionadapter
    }

    private fun toggleEmptyViewCurrentMonth(isEmpty: Boolean) {
        if (isEmpty) {
            // Show the "No data" text and hide the RecyclerView
            binding.Noadmissionsthismonth.visibility = View.VISIBLE
            binding.RecyclerAdmetion.visibility = View.GONE
        } else {
            // Show the RecyclerView and hide the "No data" text
            binding.Noadmissionsthismonth.visibility = View.GONE
            binding.RecyclerAdmetion.visibility = View.VISIBLE
        }
    }

    private fun observeBirthdaysStudents() {
        viewModelAllStudent.students.observe(viewLifecycleOwner) { students ->
            // Get today's date
            val today = Calendar.getInstance()
            val todayDateString = SimpleDateFormat("MM-dd", Locale.getDefault()).format(today.time)

            // Filter students who have their birthday today
            val birthdayStudents = students.filter { student ->
                try {
                    // Parse dt_of_birth in "dd/MM/yyyy" format
                    val birthDate = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).parse(student.dt_of_birth)
                    // Convert birthDate to "MM-dd" format for comparison with today's date
                    val formattedBirthDate =
                        SimpleDateFormat("MM-dd", Locale.getDefault()).format(birthDate)
                    formattedBirthDate == todayDateString
                } catch (e: Exception) {
                    // Handle invalid date format or empty field
                    Log.e(
                        "observeBirthdaysStudents",
                        "Invalid date format for student: ${student.dt_of_birth}"
                    )
                    false
                }
            }

            // Log for debugging
            Log.d(
                "observeBirthdaysStudents",
                "Filtered birthday students: ${birthdayStudents.size}"
            )

            // Update the adapter if there are birthday students
            if (birthdayStudents.isNotEmpty()) {
                BirthdayStudentadapter.updateBirthdayData(birthdayStudents)
                toggleEmptyView1(false)

            } else {
                Log.d("observeBirthdaysStudents", "No birthday students today")
                BirthdayStudentadapter.updateBirthdayData(emptyList())
                toggleEmptyView1(true)

            }
        }
    }

    private fun initViewBirthDayStudent() {
        // Ensure the recycler view layout is set
        binding.recyclerViewBirthDayStudent.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)

        // Use requireContext() for the context in a fragment
        val context = requireContext()  // Correctly getting the context of the fragment

        // Initialize the adapter with an empty list and context
        BirthdayStudentadapter = BirthdayStudentAdapter(emptyList(), context)

        // Set the adapter to the RecyclerView
        binding.recyclerViewBirthDayStudent.adapter = BirthdayStudentadapter
        toggleEmptyView1(true)
        // Call the function to handle cases when no birthday students are found
        // checkForNoBirthdayStudents()
    }

    private fun toggleEmptyView1(isEmpty: Boolean) {
        if (isEmpty) {
            // Show the "No data" text and hide the RecyclerView
            binding.noBirthdayTodaysStudentsTextView.visibility = View.VISIBLE
            binding.recyclerViewBirthDayStudent.visibility = View.GONE
        } else {
            // Show the RecyclerView and hide the "No data" text
            binding.noBirthdayTodaysStudentsTextView.visibility = View.GONE
            binding.recyclerViewBirthDayStudent.visibility = View.VISIBLE
        }
    }

    /*
    private fun checkForNoBirthdayStudents() {
        if (Birthdaystudent.isEmpty()) {
            // No birthdays available
            binding.noBirthdayTodaysStudentsTextView.visibility = View.VISIBLE
            binding.recyclerViewBirthDayStudent.visibility = View.GONE
            binding.noBirthdayTodaysStudentsTextView.text = "No birthdays today!"

        } else {
            // Birthdays available
            binding.noBirthdayTodaysStudentsTextView.visibility = View.GONE
            binding.recyclerViewBirthDayStudent.visibility = View.VISIBLE
        }
    }
*/

    private fun getCurrentMonthStudentCount(students: List<Student>): Int {
        val currentMonth =
            Calendar.getInstance().get(Calendar.MONTH) + 1

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )

        return students.count { student ->
            val registrationMonth = student.created_at?.let {
                try {
                    val date = dateFormat.parse(it)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.get(Calendar.MONTH) + 1
                } catch (e: Exception) {
                    Log.e("DashBoard", "Error parsing created_at date: ${student.created_at}", e)
                    null
                }
            }

            registrationMonth == currentMonth
        }
    }

    private fun SetUPListners() {
        binding.TotalStudent.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.fragment_container,
                AllStudent()
            )
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.TotalEmployees.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.fragment_container,
                AllEmployees()
            )
            transaction.addToBackStack(null)
            transaction.commit()
        }

        /* binding.btnAddStudent.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, AddStudent())
            transaction.addToBackStack(null)
            transaction.commit()
        }*/
    }

    private fun setupViewModelAllEmployees() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AllEmployeesRepository(apiService)
        val factory = AllEmployeesViewModelFactory(repository)
        viewModelAllEmployees =
            ViewModelProvider(this, factory).get(AllEmployeesViewModel::class.java)
    }

    private fun observeTotalEmployeeCount() {
        viewModelAllEmployees.employeeResponse.observe(viewLifecycleOwner) { response ->
            // Safely handle null and empty employee list
            if (response != null && response.employee != null && response.employee.isNotEmpty()) {
                val totalEmployeeCount = response.employee.size
                val currentMonthEmployeeCount = getEmployeesCountForCurrentMonth(response.employee)

                binding.totalEmployeeCountTextView.text = "$totalEmployeeCount"
                binding.TotalEmployeesforPrinciple.text = "$totalEmployeeCount"

                binding.currentMonthEmployeeCountTextView.text =
                    if (currentMonthEmployeeCount > 0) {
                        "$currentMonthEmployeeCount"
                    } else {
                        "0"
                    }
            } else {
                binding.totalEmployeeCountTextView.text = "0"
                binding.currentMonthEmployeeCountTextView.text = "0"
            }
        }
    }

    private fun fetchEmployees() {
        val schoolId = getSchoolId()
        viewModelAllEmployees.getAllEmployees(schoolId.trim())
    }

    private fun getEmployeesCountForCurrentMonth(employees: List<AllEmployee>): Int {
        val currentMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Date())

        return employees.count { employee ->
            employee.created_at.substring(
                5,
                7
            ) == currentMonth
        }
    }

    private fun observeBirthdaysEmp() {
        viewModelAllEmployees.employeeResponse.observe(viewLifecycleOwner) { response ->
            val todayDateString = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())
            val birthdayEmployees = mutableListOf<AllEmployee>()

            response?.employee?.let { employees ->
                if (employees.isNotEmpty()) {
                    employees.forEach { emp ->
                        val rawBirthDate = emp.date_of_birth
                        if (!rawBirthDate.isNullOrEmpty() && isValidDateFormat(
                                rawBirthDate,
                                "dd-MM-yyyy"
                            )
                        ) {
                            try {
                                val birthDate =
                                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(
                                        rawBirthDate
                                    )
                                val formattedBirthDate = SimpleDateFormat(
                                    "MM-dd",
                                    Locale.getDefault()
                                ).format(birthDate!!)
                                if (formattedBirthDate == todayDateString) {
                                    birthdayEmployees.add(emp)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "observeBirthdaysEmp",
                                    "Error parsing date for employee: $rawBirthDate",
                                    e
                                )
                            }
                        } else {
                            Log.e(
                                "observeBirthdaysEmp",
                                "Invalid or empty date format for employee: $rawBirthDate"
                            )
                        }
                    }
                }
            }

            // Update the RecyclerView adapter with birthday employees
            if (birthdayEmployees.isNotEmpty()) {
                empbirthdayadapter.updateBirthdayempData(birthdayEmployees)
                toggleEmptyView(false)

            } else {
                empbirthdayadapter.updateBirthdayempData(emptyList())
                toggleEmptyView(true)

            }
        }
    }

    private fun isValidDateFormat(date: String, format: String): Boolean {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.isLenient = false // Ensures strict parsing
            sdf.parse(date) != null
        } catch (e: ParseException) {
            false
        }
    }

    private fun initViewBirthDayEmp() {
        binding.recyclerViewEmployeeBirthDays.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)

        val context = requireActivity()
        empbirthdayadapter =
            BirthDayEmpAdapter(context, allemplist)  // Pass context first, then the list

        binding.recyclerViewEmployeeBirthDays.adapter = empbirthdayadapter
        // checkForNoBirthdayEmp()
        toggleEmptyView(true)
    }

    private fun toggleEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            // Show the "No data" text and hide the RecyclerView
            binding.noBirthDayEmployeesTextView.visibility = View.VISIBLE
            binding.recyclerViewEmployeeBirthDays.visibility = View.GONE
        } else {
            // Show the RecyclerView and hide the "No data" text
            binding.noBirthDayEmployeesTextView.visibility = View.GONE
            binding.recyclerViewEmployeeBirthDays.visibility = View.VISIBLE
        }
    }

    /*
    private fun checkForNoBirthdayEmp() {
        if (allemplist.isEmpty()) {
            // No birthdays available
            binding.recyclerViewEmployeeBirthDays.visibility = View.GONE
            binding.noBirthDayEmployeesTextView.visibility = View.VISIBLE
            binding.noBirthDayEmployeesTextView.text = "No birthdays today!"
        } else {
            // Birthdays are available
            binding.recyclerViewEmployeeBirthDays.visibility = View.VISIBLE
            binding.noBirthDayEmployeesTextView.visibility = View.GONE
        }
    }
*/

    private fun setupViewModelAllAttadance() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetAttendanceRepository(apiService)
        val factory = AttendanceViewModelFactory(repository)
        viewModelAllAttendance =
            ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)

        val schoolId =
            getSchoolId() // Retrieve the schoolId here (e.g., from SharedPreferences or passed as an argument)
        viewModelAllAttendance.fetchStudentAttendance(schoolId.trim()) // Pass schoolId here
    }

    private fun initViewAbsentStudent() {
        // Initialize RecyclerViews with GridLayoutManager
        binding.recyclerViewStudents.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        //  binding.recyclerViewStudentPresent.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        //  binding.recyclerViewStudentLate.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)

        // Initialize the adapters
        absentStudentAdapter = AbsentStudentAdapter(absentStudents)
        //  presentStudentAdapter = AbsentStudentAdapter(presentStudents)
        // lateStudentAdapter = AbsentStudentAdapter(lateStudents)

        // Set the adapters to respective RecyclerViews
        binding.recyclerViewStudents.adapter = absentStudentAdapter
        //binding.recyclerViewStudentPresent.adapter = presentStudentAdapter
        //binding.recyclerViewStudentLate.adapter = lateStudentAdapter
        checkForNoAbsentStudents()

    }

    private fun observeAttendanceData() {
        viewModelAllAttendance.studentAttendance.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                if (response.status) {
                    // Log the full data for debugging
                    Log.d("AttendanceData", "Received student attendance data: ${response.data}")

                    // Get today's date
                    val currentDate = getCurrentDate()

                    // Filter the data for today's attendance
                    val todayData = response.data.filter { it.date == currentDate }

                    if (todayData.isNotEmpty()) {
                        // Calculate total students for today
                        val totalStudents = todayData.size


                        updatePresentStudentsPercentage(todayData, totalStudents)
                        updateAbsentStudentsPercentage(todayData, totalStudents)
                        updateLeaveStudentsPercentage(todayData, totalStudents)

                        // Count present students
                        val presentStudents =
                            todayData.count { it.status.equals("P", ignoreCase = true) }

                        // Check if there are any present students
                        if (presentStudents > 0) {
                            // Calculate present students' percentage
                            val presentStudentsPercentage = if (totalStudents > 0) {
                                (presentStudents.toFloat() / totalStudents.toFloat()) * 100
                            } else {
                                0f

                            }

                            // Update the graph with the calculated percentage
                            updateEstimationEntries(presentStudentsPercentage)

                            // Show the graph
                            binding.horizontalBarChartPresently.visibility = View.VISIBLE
                            binding.noPresentStudentsTextView.visibility =
                                View.GONE // Hide the no data message
                        } else {
                            // No present students, show a message
                            showNoPresentStudentsMessage()
                        }

                        // Filter students based on today's data (Absent Students)
                        filterStudentsByStatus(todayData)

                        // Check for absent students
                        if (absentStudents.isEmpty()) {
                            showNoAbsentStudentsMessage()
                        } else {
                            // Update the adapter with absent students
                            absentStudentAdapter.updateData(absentStudents)
                            showAbsentStudents()
                        }

                        // Log present students' names for debugging
                        val studentNames = todayData.map { it.st_name }
                        Log.d("StudentNames", "Present Students: $studentNames")

                        Log.d("AttendanceData", "Today's attendance data processed successfully.")
                    } else {
                        // Handle case where no data is available for today
                        showNoTodayDataMessage()
                    }
                } else {
                    // Handle cases where the response status is false
                    Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show()
                    //  Log.d("AttendanceData", "No attendance data available.")
                }
            } else {
                // Handle case when response is null
//                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                // Log.e("AttendanceData", "Failed to fetch attendance data: response is null.")
            }
        })
    }

    private fun filterStudentsByStatus(students: List<AbsentStudent>) {
        absentStudents.clear()

        val currentDate = getCurrentDate()

        for (student in students) {
            if (student.status == "A" && student.date == currentDate) {
                absentStudents.add(student)

            }
        }
    }

    private fun updatePresentStudentsPercentage(
        todayData: List<AbsentStudent>,
        totalStudents: Int
    ) {
        val presentStudents = todayData.count { it.status.equals("P", ignoreCase = true) }
        val presentPercentage = if (totalStudents > 0) {
            (presentStudents.toFloat() / totalStudents) * 100
        } else {
            0f
        }

        // Update the progress with animation or immediately
        binding.StudentPresentyForPrincipal.setProgressWithAnimation(presentPercentage)

        // Log the present percentage for debugging
        Log.d("AttendanceData", "Present percentage: $presentPercentage")
    }

    private fun updateAbsentStudentsPercentage(todayData: List<AbsentStudent>, totalStudents: Int) {
        absentStudents.clear()
        absentStudents.addAll(todayData.filter { it.status.equals("A", ignoreCase = true) })

        val absentPercentage = if (totalStudents > 0) {
            (absentStudents.size.toFloat() / totalStudents) * 100
        } else {
            0f
        }

        binding.StudentAbsentyForPrincipal.setProgressWithAnimation(absentPercentage)
        Log.d("AttendanceData", "Absent percentage: $absentPercentage")
    }

    private fun updateLeaveStudentsPercentage(todayData: List<AbsentStudent>, totalStudents: Int) {
        val leaveStudents = todayData.count { it.status.equals("L", ignoreCase = true) }
        val leavePercentage = if (totalStudents > 0) {
            (leaveStudents.toFloat() / totalStudents) * 100
        } else {
            0f
        }

        binding.StudentLeaveForPrincipal.setProgressWithAnimation(leavePercentage)
        Log.d("AttendanceData", "Leave percentage: $leavePercentage")
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return String.format("%04d-%02d-%02d", year, month, day)
    }

    private fun showNoTodayDataMessage() {
        binding.recyclerViewStudents.visibility = View.GONE
        binding.noAbsentStudentsTextView.text = "Today's students are not marked."
        binding.noAbsentStudentsTextView.visibility = View.VISIBLE
    }

    private fun showNoAbsentStudentsMessage() {
        binding.recyclerViewStudents.visibility = View.GONE
        binding.noAbsentStudentsTextView.text = "Today's students are not marked."
        binding.noAbsentStudentsTextView.visibility = View.VISIBLE
    }

    private fun showAbsentStudents() {
        binding.recyclerViewStudents.visibility = View.VISIBLE
        binding.noAbsentStudentsTextView.visibility = View.GONE
    }

    private fun checkForNoAbsentStudents() {
        if (absentStudents.isEmpty()) {
            binding.recyclerViewStudents.visibility = View.GONE
            binding.noAbsentStudentsTextView.visibility =
                View.VISIBLE
        } else {
            binding.recyclerViewStudents.visibility = View.VISIBLE
            binding.noAbsentStudentsTextView.visibility =
                View.GONE
        }
    }

    private fun setupViewModelAttendanceEmployee() {
        val apiService = RetrofitHelper.getApiService()
        val repository = EmloyeesAttendanceRepository(apiService)
        val factory = EmployeesAttendanceViewModelFactory(repository)
        viewModelAttendanceEmployees =
            ViewModelProvider(this, factory).get(EmployeesAttendanceViewModel::class.java)

        val schoolId = getSchoolId()
        viewModelAttendanceEmployees.getEmployeeAttendance(schoolId.trim())
    }

    private fun initViewAbsentEmployees() {
        // Log the data being passed to the adapter
        // Log.d("AttendanceData", "Absent employees list before setting adapter: $absentEmployees")

        // Ensure RecyclerView is properly set up
        binding.recyclerViewEmployeeAbsent.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)

        // Log the adapter creation and data assignment
        absentEmployeesAdapter = EmployeeAttandanceAdapter(absentEmployees)
        binding.recyclerViewEmployeeAbsent.adapter = absentEmployeesAdapter

        // Log after setting the adapter
        Log.d("AttendanceData", "Adapter set with data: $absentEmployees")
    }

    private fun observeEmployeesAttendanceData() {
        viewModelAttendanceEmployees.employeeAttendanceData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null && response.status) {
                    // Log the full response for debugging
                    // Log.d("AttendanceData", "Received employee attendance data: ${response.data}")

                    // Get today's date
                    val currentDate = getCurrentDate()

                    // Filter data for today's attendance
                    val todayData = response.data.filter { it.date == currentDate }

                    if (todayData.isNotEmpty()) {
                        // Calculate total employees for today
                        val totalEmployees = todayData.size
                        updatePresentEmployeesPercentage(todayData, totalEmployees)
                        updateAbsentEmployeesPercentage(todayData, totalEmployees)
                        updateLeaveEmployeesPercentage(todayData, totalEmployees)

                        // Count present employees
                        val presentEmployeesCount =
                            todayData.count { it.status.equals("P", ignoreCase = true) }

                        // Calculate present employees percentage
                        val presentEmployeesPercentage = if (totalEmployees > 0) {
                            (presentEmployeesCount.toFloat() / totalEmployees.toFloat()) * 100
                        } else {
                            0f
                        }

                        // Log the calculated percentage


                        // Now check if there are any present employees
                        if (presentEmployeesCount > 0) {
                            // If present employees exist, update the chart with the calculated percentage
                            updateRemainingEntries(presentEmployeesPercentage)
                            binding.horizontalBarChartPresently.visibility =
                                View.VISIBLE // Show the graph
                            binding.noPresentEmployeesTextView.visibility =
                                View.GONE // Hide the text message
                        } else {
                            // If no present employees, show the text message
                            showNoPresentEmployeesMessage()
                        }

                        // Filter and display absent employees
                        filterEmployeesByStatus(todayData)

                        if (absentEmployees.isEmpty()) {
                            showNoAbsentEmployeesMessage()
                        } else {
                            updateAbsentEmployeesList()
                        }

                    } else {
                        // No data available for today
                        showNoTodayDataEmpMessage()
                    }
                } else {
                    // Handle cases where response is null or status is false
                    showDataFetchErrorMessage()
                }
            }
        )
    }

    private fun showNoTodayDataEmpMessage() {
        binding.recyclerViewEmployeeAbsent.visibility = View.GONE
        binding.noAbsentEmployeesTextView.text = "Today's employees are not marked."
        binding.noAbsentEmployeesTextView.visibility = View.VISIBLE
    }

    private fun updateAbsentEmployeesList() {
        binding.recyclerViewEmployeeAbsent.visibility = View.VISIBLE
        binding.noAbsentEmployeesTextView.visibility = View.GONE
        absentEmployeesAdapter.updateData(absentEmployees)
    }

    private fun showDataFetchErrorMessage() {
        binding.recyclerViewEmployeeAbsent.visibility = View.GONE
        binding.noAbsentEmployeesTextView.text = "Today's Employees are not marked."
        binding.noAbsentEmployeesTextView.visibility = View.VISIBLE
        //  Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
        Log.e(
            "AttendanceData",
            "Failed to fetch employee attendance data: response is null or invalid."
        )
    }


    private fun showNoAbsentEmployeesMessage() {
        binding.recyclerViewEmployeeAbsent.visibility = View.GONE
        binding.noAbsentEmployeesTextView.text = "No absent employees."
        binding.noAbsentEmployeesTextView.visibility = View.VISIBLE
    }


    private fun filterEmployeesByStatus(employees: List<EmployeeAttendance>) {
        absentEmployees.clear()

        val currentDate = getCurrentDate()

        for (employee in employees) {
            if (employee.status == "A" && employee.date == currentDate) {
                absentEmployees.add(employee)
            }
        }

        Log.d("AttendanceData", "Filtered absent employees for today: $absentEmployees")
    }

    private fun updatePresentEmployeesPercentage(
        todayData: List<EmployeeAttendance>,
        totalEmployees: Int
    ) {
        val presentEmployees = todayData.count { it.status.equals("P", ignoreCase = true) }
        val presentPercentage = if (totalEmployees > 0) {
            (presentEmployees.toFloat() / totalEmployees) * 100
        } else {
            0f
        }

        binding.EmployeePresentyForPrincipal12.setProgressWithAnimation(presentPercentage)
        Log.d("AttendanceData", "Present percentage: $presentPercentage")
    }

    // Update percentage of absent employees
    private fun updateAbsentEmployeesPercentage(
        todayData: List<EmployeeAttendance>,
        totalEmployees: Int
    ) {
        absentEmployees.clear()
        absentEmployees.addAll(todayData.filter { it.status.equals("A", ignoreCase = true) })

        val absentPercentage = if (totalEmployees > 0) {
            (absentEmployees.size.toFloat() / totalEmployees) * 100
        } else {
            0f
        }

        binding.EmployeeAbsentyForPrincipal.setProgressWithAnimation(absentPercentage)
        Log.d("AttendanceData", "Absent percentage: $absentPercentage")
    }

    // Update percentage of employees on leave
    private fun updateLeaveEmployeesPercentage(
        todayData: List<EmployeeAttendance>,
        totalEmployees: Int
    ) {
        val leaveEmployees = todayData.count { it.status.equals("L", ignoreCase = true) }
        val leavePercentage = if (totalEmployees > 0) {
            (leaveEmployees.toFloat() / totalEmployees) * 100
        } else {
            0f
        }

        binding.EmployeeLeaveForPrincipal.setProgressWithAnimation(leavePercentage)
        Log.d("AttendanceData", "Leave percentage: $leavePercentage")
    }

    private fun initData() {
    }

    private fun setUpListeners() {
    }


    private fun setviewmodeloverview() {
        val apiService = RetrofitHelper.getApiService()
        val repository = getOverviewRepository(apiService)
        val factory = getoverViewModelFactory(repository)
        viewmodelOverview = ViewModelProvider(this, factory).get(getOverViewModel::class.java)
        val schoolId = getSchoolId()
        viewmodelOverview.fetchOverView(schoolId.trim())
    }

    private fun observalOverView() {
        viewmodelOverview.overviewchart.observe(viewLifecycleOwner, Observer { response ->
            Log.d("profitData", "Raw response: $response")

            response?.let {
                Log.d("profitData", "Data status: ${it.status}")
                val overchartData = it.data
                if (overchartData != null) {
                    // Reverse the data order before passing it to the chart initialization function
                    val reversedData = overchartData.reversed()
                    initLineChartData(reversedData)
                } else {
                    Log.d("profitData", "No data available")
                }
            } ?: run {
                Log.d("profitData", "Response is null")
            }
        })
    }

    private fun initLineChartData(overviewData: List<OverViewData>) {
        val incomeEntries = ArrayList<Entry>()
        val expenseEntries = ArrayList<Entry>()

        var maxValue = 0f

        // Populate entries and find the maximum value
        for (i in overviewData.indices) {
            val data = overviewData[i]
            val deposit = data.month_income.toFloat()
            val expense = data.month_expense.toFloat()

            incomeEntries.add(Entry(i.toFloat(), deposit))
            expenseEntries.add(Entry(i.toFloat(), expense))

            maxValue = maxOf(maxValue, deposit, expense)
        }

        // Round up the maximum value to the nearest multiple of 10 or 100
        val axisMax = ((maxValue / 100).toInt() + 1) * 100f
        val granularity = axisMax / 10

        // Create LineDataSet for income with cubic lines
        val incomeDataSet = LineDataSet(incomeEntries, "Income")
        incomeDataSet.color = resources.getColor(R.color.green)
        incomeDataSet.valueTextColor = Color.BLACK
        incomeDataSet.lineWidth = 2f
        incomeDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Enable cubic lines
        incomeDataSet.setDrawCircles(true)
        incomeDataSet.setCircleColor(Color.BLACK)
        incomeDataSet.setDrawFilled(true)
        incomeDataSet.fillDrawable = resources.getDrawable(R.drawable.gradient_green, null)

        // Create LineDataSet for expense with cubic lines
        val expenseDataSet = LineDataSet(expenseEntries, "Expense")
        expenseDataSet.color = resources.getColor(R.color.red)
        expenseDataSet.valueTextColor = Color.BLACK
        expenseDataSet.lineWidth = 2f
        expenseDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        expenseDataSet.setDrawCircles(true)
        expenseDataSet.setCircleColor(Color.BLACK)
        expenseDataSet.setDrawFilled(true)
        expenseDataSet.fillDrawable = resources.getDrawable(R.drawable.gradient_red, null)

        val lineData = LineData(incomeDataSet, expenseDataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            private val months = overviewData.map { it.month }.toTypedArray()
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return if (value >= 0 && value < months.size) months[value.toInt()] else ""
            }
        }
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK
        xAxis.spaceMin = 0.5f // Add padding at the start
        xAxis.spaceMax = 0.5f // Add padding at the end

        // Configure Y-Axis
        val yAxisLeft: YAxis = lineChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = axisMax
        yAxisLeft.granularity = granularity
        yAxisLeft.textColor = Color.BLACK
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.spaceTop = 10f // Add padding at the top for better spacing

        val yAxisRight: YAxis = lineChart.axisRight
        yAxisRight.isEnabled = false

        // Customize chart appearance
        lineChart.description.isEnabled = false
        lineChart.setExtraOffsets(10f, 10f, 10f, 10f) // Add padding around the chart

        lineChart.legend.isEnabled = true
        lineChart.legend.textColor = Color.BLACK
        lineChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        lineChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        lineChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL

        // Refresh chart
        lineChart.invalidate()
    }

    private fun setupViewModelAllClasses() {
        val repository = AllClassRepository()
        val factory = AllClassViewModelFactory(repository)
        viewModelAllClasses = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)
    }

    private fun observeAllClasses() {
        val schoolId = getSchoolId()?.trim() ?: ""

        viewModelAllClasses.getClasses(schoolId).observe(viewLifecycleOwner) { response ->
            response?.data?.let {
                // Pass the class data to the chart initialization method
                initBarChartAllClassesData(it)
            }
        }
    }

    private fun initBarChartAllClassesData(classData: List<ClassItem>) {
        val barEntries = ArrayList<BarEntry>()
        val classLabels = ArrayList<String>()

        classData.forEachIndexed { index, data ->
            data.total_students?.let {
                barEntries.add(BarEntry(index.toFloat(), it.toFloat()))
                classLabels.add(data.class_name ?: "Class ${index + 1}")
            }
        }

        val barDataSet = BarDataSet(barEntries, "Classes Strength")
        barDataSet.color = resources.getColor(R.color.studentColor)
        barDataSet.valueTextColor = resources.getColor(R.color.white)
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(classLabels)
        xAxis.setLabelCount(classData.size, false)

        val yAxis = barChart.axisLeft
        yAxis.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        barChart.setVisibleXRangeMaximum(4f)
        barChart.isDragEnabled = true
        barChart.setScaleEnabled(false)

        barChart.invalidate()
    }

    private fun setupViewModelCollectionStatus() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetCollectionStatusRepository(apiService)
        val factory = GetCollectionStatusVIewModelFactory(repository)
        ViewmodelCollectionStatus =
            ViewModelProvider(this, factory).get(GetCollectionStatusViewModel::class.java)
        val schoolId = getSchoolId()
        ViewmodelCollectionStatus.fetchCollection(schoolId.trim())


    }

    private fun observeCollection() {
        ViewmodelCollectionStatus.collection.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                //Log.d("CollectionData", "Data status: ${it.status}")
                val collectionData = it.data
                if (collectionData != null) {
                    // Pass data to initEstimationRemainingData
                    initEstimationRemainingData(
                        collectionData.collection_percentage.toFloat(),
                        collectionData.remaining_percentage.toFloat(),
                        collectionData.total_deposit.toFloat(),
                        collectionData.remaining_balance.toFloat()
                    )
                }
            }
        })
    }

    private fun initEstimationRemainingData(
        collectionPercentage: Float,
        remainingPercentage: Float,
        totalDeposit: Float,
        remainingBalance: Float
    ) {
        val remainingEntries = ArrayList<BarEntry>().apply {
            add(BarEntry(0f, remainingPercentage))
        }

        val plannedEntries = ArrayList<BarEntry>().apply {
            add(BarEntry(1f, collectionPercentage))
        }

        val context = requireContext()
        val plannedDataSet = BarDataSet(plannedEntries, "Collection").apply {
            color = ContextCompat.getColor(context, R.color.green)
            valueTextColor = ContextCompat.getColor(context, R.color.white)
        }
        val remainingDataSet = BarDataSet(remainingEntries, "Remaining").apply {
            color = ContextCompat.getColor(context, R.color.red)
            valueTextColor = ContextCompat.getColor(context, R.color.white)
        }

        val barData = BarData(remainingDataSet, plannedDataSet).apply {
            barWidth = 0.3f
        }

        horizontalBarChart.data = barData

        // Customize X-Axis
        val xAxis = horizontalBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.granularity = 1f
        xAxis.textSize = 10f
        xAxis.setDrawGridLines(false)
        xAxis.spaceMin = 0.5f
        xAxis.spaceMax = 0.5f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value.toInt()) {
                    0 -> "Remaining"
                    1 -> "Collection"
                    else -> ""
                }
            }
        }

        // Customize Y-Axis
        val yAxisLeft = horizontalBarChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = 100f
        yAxisLeft.granularity = 1f
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}%"
            }
        }

        horizontalBarChart.axisRight.isEnabled = false
        horizontalBarChart.description.isEnabled = false
        horizontalBarChart.setFitBars(true)
        horizontalBarChart.setExtraOffsets(40f, 40f, 40f, 40f)
        horizontalBarChart.invalidate()

        val chartDetails = binding.chartDetails // Reference the TextView

        horizontalBarChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let {
                    when (e.x.toInt()) {
                        0 -> {
                            chartDetails.text = "Remaining Balance: ₹$remainingBalance"
                        }

                        1 -> {
                            chartDetails.text = "Total Deposit: ₹$totalDeposit"
                        }
                    }
                }
            }

            override fun onNothingSelected() {
                chartDetails.text = ""
            }
        })
    }


    private fun showBalanceToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private fun showNoPresentStudentsMessage() {
        // Hide RecyclerView if no data available
        binding.horizontalBarChartPresently.visibility = View.GONE
        // Show the message indicating no present students today
        binding.noPresentStudentsTextView.text = "Today's students are not present."
        binding.noPresentStudentsTextView.visibility = View.VISIBLE
    }

    private fun showNoPresentEmployeesMessage() {
        // Hide RecyclerView if no data available
        binding.horizontalBarChartPresently.visibility = View.GONE
        // Show the message indicating no present employees today
        binding.noPresentEmployeesTextView.text = "No present employees today."
        binding.noPresentEmployeesTextView.visibility = View.VISIBLE
    }

    private fun showNoPresentStudentsMessageAreNotMark() {
        binding.horizontalBarChartPresently.visibility = View.GONE  // Hide RecyclerView
        binding.noPresentStudentsTextView.text = "Today's students are not marked."
        binding.noPresentStudentsTextView.visibility = View.VISIBLE  // Show the message
    }

    private fun showNoTodayDataEmp() {
        binding.recyclerViewEmployeeAbsent.visibility = View.GONE
        binding.noPresentEmployeesTextView.text = "Today's employees are not marked."
        binding.noPresentEmployeesTextView.visibility = View.VISIBLE
    }


    /*private fun showNoFeesCollectionMessage() {
        // Hide RecyclerView if no data available
        binding.recyclerViewFees.visibility = View.GONE
        // Show the message indicating no fees collection
        binding.noFeesCollectionTextView.text = "No fees collected today."
        binding.noFeesCollectionTextView.visibility = View.VISIBLE
    }
*/

    private fun updateEstimationEntries(presentStudentsPercentage: Float) {
        // Clear the old data and add the new one
        estimationEntries.clear()
        estimationEntries.add(BarEntry(0f, presentStudentsPercentage))
        updateChart() // Refresh the chart after adding the new data
    }

    // Update Remaining Entries with the data for Present Employees
    private fun updateRemainingEntries(presentEmployeesPercentage: Float) {
        // Clear the old data and add the new one
        remainingEntries.clear()
        remainingEntries.add(BarEntry(1f, presentEmployeesPercentage))
        updateChart() // Refresh the chart after adding the new data
    }

    // Update Planned Entries with the data for Fees Collection
    private fun updatePlannedEntries(feesCollectionPercentage: Float) {
        // Clear the old data and add the new one
        plannedEntries.clear()
        plannedEntries.add(BarEntry(2f, feesCollectionPercentage))
        updateChart() // Refresh the chart after adding the new data
    }

    private fun updateChart() {
        val context = requireContext()

        // Check if data for Present Students, Present Employees, and Fees Collection is available
        val isPresentStudentsAvailable = estimationEntries.isNotEmpty()
        val isPresentEmployeesAvailable = remainingEntries.isNotEmpty()
        val isFeesCollectionAvailable = plannedEntries.isNotEmpty()

        // Show appropriate message if no data is available
        if (!isPresentStudentsAvailable) {
            showNoPresentStudentsMessage()
        } else {
            binding.horizontalBarChartPresently.visibility =
                View.VISIBLE // Ensure chart is visible if data is available
        }

        if (!isPresentEmployeesAvailable) {
            showNoPresentEmployeesMessage()
        } else {
            binding.horizontalBarChartPresently.visibility =
                View.VISIBLE // Ensure chart is visible if data is available
        }

        // Creating BarDataSet for each category
        val estimationDataSet = BarDataSet(estimationEntries, "Present Students").apply {
            color = ContextCompat.getColor(context, R.color.studentColor)
            valueTextColor = ContextCompat.getColor(context, R.color.white)
            valueTextSize = 14f
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    return "${barEntry?.y?.toInt()}%"  // Adding percentage sign to the value
                }
            }
        }

        val remainingDataSet = BarDataSet(remainingEntries, "Present Employees").apply {
            color = ContextCompat.getColor(context, R.color.employeeColor)
            valueTextColor = ContextCompat.getColor(context, R.color.white)
            valueTextSize = 14f
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    return "${barEntry?.y?.toInt()}%"  // Adding percentage sign to the value
                }
            }
        }

        val plannedDataSet = BarDataSet(plannedEntries, "Fees Collection").apply {
            color = ContextCompat.getColor(context, R.color.global_button_background)
            valueTextColor = ContextCompat.getColor(context, R.color.white)
            valueTextSize = 14f
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    return "${barEntry?.y?.toInt()}%"  // Adding percentage sign to the value
                }
            }
        }

        // Add datasets only if data is available
        val datasets = mutableListOf<IBarDataSet>()
        if (isPresentStudentsAvailable) datasets.add(estimationDataSet)
        if (isPresentEmployeesAvailable) datasets.add(remainingDataSet)
        if (isFeesCollectionAvailable) datasets.add(plannedDataSet)

        // If no data is available for all categories, hide the chart
        if (datasets.isEmpty()) {
            binding.horizontalBarChartPresently.visibility = View.GONE
            Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show()
            return
        }

        // Continue with your BarData setup
        val barData = BarData(datasets)
        barData.barWidth = 0.3f

        // Update chart with new data
        horizontalBarChartPresently.data = barData

        // Configure the X-Axis
        val xAxis = horizontalBarChartPresently.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value.toInt()) {
                    0 -> if (isPresentStudentsAvailable) "Today Present Students" else "Not Available"
                    1 -> if (isPresentEmployeesAvailable) "Today Present Employees" else "Not Available"
                    2 -> if (isFeesCollectionAvailable) "Fees Collection" else "Not Available"
                    else -> ""
                }
            }
        }

        // Configure the Y-Axis
        val yAxisLeft = horizontalBarChartPresently.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = 100f
        yAxisLeft.granularity = 1f
        yAxisLeft.setDrawGridLines(true)

        // Apply value formatter to the Y-Axis
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}%"  // Adding percentage sign to the value
            }
        }

        horizontalBarChartPresently.description.isEnabled = false
        horizontalBarChartPresently.setFitBars(false)
        horizontalBarChartPresently.setDrawValueAboveBar(false)
        horizontalBarChartPresently.invalidate()
    }

    private fun fetchCalender() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetCalenderRepository(apiService)
        val factory = GetCalenderViewModelFactory(repository)
        viewmodelcalender = ViewModelProvider(this, factory).get(GetCalenderViewModel::class.java)

        val schoolId = getSchoolId()
        viewmodelcalender.fetchCalender(schoolId.trim())
    }

    private fun observeCalendar() {
        viewmodelcalender.Callender.observe(viewLifecycleOwner) { response ->
            response?.data?.let { eventList ->
                this.eventList = eventList
                highlightEvents(eventList, requireContext()) // Call event highlighter
                Log.d("Calendar", "Events loaded: ${eventList.size}")
            } ?: Log.d("Calendar", "No events data found in response")
        }

        // Set listener for date click event
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDate = eventDay.calendar.time
                val clickedDateStr =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(clickedDate)
                val matchingEvents = eventList.filter { it.date == clickedDateStr }

                if (matchingEvents.isNotEmpty()) {
                    showEventDialog(matchingEvents)
                } else {
                    Toast.makeText(requireContext(), "No events on this date", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun highlightEvents(events: List<CalendarEvent>, context: Context) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val eventDays = mutableListOf<EventDay>()

        for (event in events) {
            try {
                val eventDate = dateFormat.parse(event.date)
                eventDate?.let {
                    val calendar = Calendar.getInstance().apply { time = it }
                    val eventIcon = getEventDrawable(context, event.type)

                    eventIcon?.let { icon ->
                        eventDays.add(EventDay(calendar, icon))
                        Log.d(
                            "Calendar",
                            "Highlighted Event: ${event.description}, Date: ${event.date}, Type: ${event.type}"
                        )
                    }
                }
            } catch (e: ParseException) {
                Log.e("Calendar", "Error parsing event date: ${event.date}", e)
            }
        }

        binding.calendarView.setEvents(eventDays) // Apply events to CalendarView
    }

    private fun getEventDrawable(context: Context, type: String): Drawable? {
        return when (type) {
            "Holidays" -> ContextCompat.getDrawable(context, R.drawable.ic_holiday)
            "Event" -> ContextCompat.getDrawable(context, R.drawable.ic_event_launcher)
            else -> ContextCompat.getDrawable(
                context,
                R.drawable.ic_calendar
            ) // Default fallback
        }
    }

    private fun showEventDialog(events: List<CalendarEvent>) {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(SpannableString("Events on this day").apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        })

        val spannableString = SpannableStringBuilder()

        for (event in events) {
            val eventText = "${event.description}\n"
            val start = spannableString.length
            spannableString.append(eventText)

            when (event.type) {
                "Holidays" -> {
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.RED),
                        start,
                        spannableString.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        StyleSpan(Typeface.BOLD),
                        start,
                        spannableString.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                "Event" -> {
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.GREEN),
                        start,
                        spannableString.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannableString.setSpan(
                        StyleSpan(Typeface.BOLD),
                        start,
                        spannableString.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        builder.setMessage(spannableString)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()

        // Apply padding and margin
        val textView = dialog.findViewById<TextView>(android.R.id.message)
        textView?.setPadding(40, 20, 40, 20)
        textView?.textSize = 16f
    }


// For highlighting dates, you can use a third-party library.
        // Here is an example using `MaterialCalendarView` or any other calendar library that supports it.

    private fun adminDetails(){
       // val calendarView = binding.calendarView // Directly access via binding
        binding.scrollViewAdmin.visibility = View.VISIBLE
        binding.layoutPrinciple.visibility = View.GONE
        binding.EmployeeAttendanceForPrincipal.visibility=View.GONE
        binding.StudentAttendanceForPrincipal.visibility=View.GONE
        setupViewModelAllAttadance()
        setupViewModelAllStudent()
        setupViewModelAllEmployees()
        setupViewModelAttendanceEmployee()
        observeEmployeesAttendanceData()
        setupViewModelAllClasses()
        observeAllClasses()
        //  loadStudentCount()
        // observeStudentCount()
        observeAttendanceData()
        initViewAbsentEmployees()
        initViewAbsentStudent()
        initViewBirthDayStudent()
        initData()
        setUpListeners()
        observeStudents()

        initViewAdmetion()
        observeBirthdaysStudents()
        SetUPListners()
        //initBarChartAllClassesData()
        // initPresentlyData()
        getSchoolId()
        fetchEmployees()
        observeBirthdaysEmp()
        initViewBirthDayEmp()

        fetchStudent()
        observeTotalEmployeeCount()
        observeAttendanceData()
        setupViewModelRevenuest()
        observalRevenues()
        setupViewModelCollectionStatus()
        observeCollection()
        fetchRevenue()
        setupViewModelProfit()
        observalProfit()
        setviewmodeloverview()
        observalOverView()
        fetchCalender()
        observeCalendar()
    }
    private fun teacherDetails(){
        binding.scrollViewStudent.visibility = View.VISIBLE
        binding.layoutClass.visibility=View.GONE
        binding.layoutRegistrationId.visibility=View.GONE
        binding.DateofAdd.visibility=View.GONE
        binding.layoutMotherName.visibility=View.GONE
        isEmployeeActive()
        showTeacherDetail()
        getEmployeeAttence()
    }

    //   ----------------student dash visible------------------
    private fun studentDetails(){
        binding.scrollViewStudent.visibility = View.VISIBLE
        binding.layoutJoiningDate.visibility=View.GONE
        binding.layoutEmployeeId.visibility=View.GONE
        isStudentActive()
        showStudentDetail()
        loadAttencesStudent()
        observeHomeworkList()
    }
    //   ----------------principal dash visible------------------
    private fun principalDetails(){

        employeeViewModel.employeeStatus.observe(requireActivity(), Observer { response ->
            // Handle the successful response
            response?.let {
                // Update your UI with the response data
                Toast.makeText(requireContext(), it.status, Toast.LENGTH_SHORT).show()
            }
        })

        // Observing LiveData for error messages
        employeeViewModel.errorMessage.observe(requireActivity(), Observer { errorMessage ->
            // Handle error messages
            errorMessage?.let {
                Toast.makeText(requireContext(), "Massage Not available", Toast.LENGTH_SHORT).show()
            }
        })

        employeeViewModel.getEmployeeStatus(requireContext(),SchoolId().getSchoolId(requireContext()),TeacherDetails().getEmployeeID(requireContext()))

        binding.scrollViewAdmin.visibility = View.VISIBLE
        binding.layoutTotalProfit.visibility = View.GONE
        binding.layoutTotalRevenus.visibility = View.GONE
        binding.TotalStudent.visibility=View.GONE
        binding.TotalEmployees.visibility=View.GONE
        binding.scrollViewStudent.visibility = View.VISIBLE
        binding.layoutStudent.visibility = View.VISIBLE
        binding.layoutPrinciple.visibility = View.VISIBLE
        binding.classhide.visibility=View.GONE
        binding.gridLayout.columnCount = 4
        binding.accountOverview.visibility=View.GONE
        binding.feesCollection.visibility=View.GONE

        binding.showmypersonalinfoprincipal.setOnClickListener {
            animationLinearLayoutpriciple(
                binding.layoutprincipalpersonalinfo,
                0, // Collapsed height
                600, // Target height for expanded state
                500 // Duration of the animation
            )
        }

        binding.layoutprincipalpersonalinfo.layoutParams.height = 0
        binding.layoutprincipalpersonalinfo.requestLayout()


//        loadDashBoardCirclePrincipal()


        showPrincipleDetail()
        setupViewModelAllAttadance()
        setupViewModelAllStudent()
        setupViewModelAllEmployees()
        setupViewModelAttendanceEmployee()
        observeEmployeesAttendanceData()
        setupViewModelAllClasses()
        observeAllClasses()
        //  loadStudentCount()
        // observeStudentCount()
        observeAttendanceData()
        initViewAbsentEmployees()
        initViewAbsentStudent()
        initViewBirthDayStudent()
        observeBirthdaysStudents()
        initData()
        setUpListeners()
        observeStudents()
        initViewAdmetion()
        SetUPListners()
        //initBarChartAllClassesData()

        getSchoolId()
        fetchEmployees()
        observeBirthdaysEmp()
        initViewBirthDayEmp()
        fetchStudent()
        observeTotalEmployeeCount()
        observeAttendanceData()
        showStudentDetail()
        loadAttencesStudent()
        observeHomeworkList()
        loadDashBoardCirclePrincipal()
        getEmployeeAttence()
    }
    //   ----------------method show my profile for student ------------------
    private fun extendShowMyProfile(){
        // Toggle visibility of the layout
        if (binding.layoutstudentpersonalinfo.visibility == View.GONE) {
            binding.layoutstudentpersonalinfo.visibility = View.VISIBLE
            binding.showmypersonalinfo.text = "Hide My Personal Information"
//                binding.arrowdown.setImageResource(R.drawable.arrow_up)
            binding.arrow.setImageResource(R.drawable.arrow_circle_up_svgrepo_com)
        } else {
            binding.layoutstudentpersonalinfo.visibility = View.GONE
            binding.showmypersonalinfo.text = "Show My Personal Information"
//                binding.arrowdown.setImageResource(R.drawable.arrow_down)
            binding.arrow.setImageResource(R.drawable.arrow_circle_down_svgrepo_com)
        }
    }
    //student dashboard details//
    private fun showStudentDetail(){
        binding.txtStudentName.setText(stdetails.getStudentName(requireContext()))
        binding.stStudentName.setText(stdetails.getStudentName(requireContext()))
        binding.txtFatherName.setText(stdetails.getStudentFatherName(requireContext()))
        binding.studentClass.setText(stdetails.getStudentClass(requireContext()))
        binding.txtRollNo.setText(stdetails.getStudentRegistrationId(requireContext()))
        binding.etregistrationNo.setText(stdetails.getStudentRegistrationId(requireContext()))
        binding.etschoolId.setText(SchoolId().getSchoolId(requireContext()))
        binding.txtStudentClass.setText(stdetails.getStudentClass(requireContext()))
        binding.etDateOfAdmission.setText(stdetails.getStudentDtofAdd(requireContext()))
        binding.etStatus.setText(stdetails.getStudentStatus(requireContext()))
        binding.etPhoneNumber.setText(stdetails.getStudentPhoneNumber(requireContext()))
        binding.etAddress.setText(stdetails.getStudentAddress(requireContext()))
        binding.etFatherName.setText(stdetails.getStudentFatherName(requireContext()))
        binding.etMotherName.setText(stdetails.getStudentMotherName(requireContext()))
        binding.StSchoolName.setText(stdetails.getStudentSchoolName(requireContext()))
        MethodLibrary().displayImage("${ImageUtil.BASE_URL_IMAGE}$StudentImage${stdetails.getStudentPicture(requireContext())}",binding.imgstudent, requireContext())
    }

    private fun showPrincipleDetail(){
        binding.tvPrincipaleName.setText(PrincipalDetails().getName(requireContext()))
        binding.txtPrincipalName.setText(PrincipalDetails().getName(requireContext()))
        binding.tvPrincipalFatherName.setText(PrincipalDetails().getFatherName(requireContext()))
        binding.tvPrincipalRole.setText(PrincipalDetails().getRole(requireContext()))
        binding.txtSchoolName.setText(PrincipalDetails().getSchoolName(requireContext()))
        binding.txtJoiningDate.setText(PrincipalDetails().getJoiningDate(requireContext()))
        binding.txtStatus.setText(PrincipalDetails().getStatus(requireContext()))
        binding.txtPhoneNo.setText(PrincipalDetails().getPrincipalNumber(requireContext()))
        binding.txtAddress.setText(PrincipalDetails().getHomeAddress(requireContext()))
        binding.txtPrincipalFatherName.setText(PrincipalDetails().getFatherName(requireContext()))
        MethodLibrary().displayImage("${ImageUtil.BASE_URL_IMAGE}$PrincipalImage${PrincipalDetails().getPrincipleImage(requireContext())}",binding.imgPrincipalProfile,requireContext())
    }

    private fun showTeacherDetail(){
        binding.txtStudentName.setText(teacherDetails.getName(requireContext()))
        binding.stStudentName.setText(teacherDetails.getName(requireContext()))
        binding.txtFatherName.setText(teacherDetails.getFatherName(requireContext()))
        binding.etFatherName.setText(teacherDetails.getFatherName(requireContext()))
        binding.etschoolId.setText(teacherDetails.getEmployeeID(requireContext()))
        binding.TeacherJoiningDate.setText(teacherDetails.getJoiningDate(requireContext()))
        binding.etStatus.setText(teacherDetails.getStatus(requireContext()))
        binding.etPhoneNumber.setText(teacherDetails.getTeacherNumber(requireContext()))
        binding.etAddress.setText(teacherDetails.getHomeAddress(requireContext()))
        binding.studentClass.setText(teacherDetails.getRole(requireContext()))
        binding.StSchoolName.setText(teacherDetails.getSchoolName(requireContext()))
        MethodLibrary().displayImage("${ImageUtil.BASE_URL_IMAGE}$InstituteLogo${teacherDetails.getSchoolLogo(requireContext())}",binding.imgSchoolLogo, requireContext())
        MethodLibrary().displayImage("${ImageUtil.BASE_URL_IMAGE}$PrincipalImage${teacherDetails.getProfile(requireContext())}",binding.imgstudent, requireContext())

    }

    //   ----------------Check Role And redirect ------------------
    private fun CheckRole() {
        when (SchoolId().getLoginRole(requireContext())) {
            "Student" -> studentDetails()
            "Admin" -> adminDetails()
            "Principal" -> principalDetails()
            "Teacher" ->  teacherDetails()
        }
    }
    //   ----------------Load DashBoard Circle ------------------
    private fun loadDashBoardCircle() {
        circularProgressViewPresent = binding.circularProgressViewPresent
        circularProgressViewAbsent = binding.circularProgressViewAbsent
        circularProgressViewLeave = binding.circularProgressViewLeave
    }

    private fun loadDashBoardCirclePrincipal() {
        circularProgressViewPresentPricipal = binding.circularProgressViewPresentPrincipal
        circularProgressViewAbsentPricipal = binding.circularProgressViewAbsentPrincipal
        circularProgressViewLeavePricipal = binding.circularProgressViewLeavePrincipal
    }


    //   ----------------Load DashBoard Data From Network Student------------------
    private fun loadAttencesStudent() {
        // Initialize ViewModel
        val repository = StudentPresentlyRepository(RetrofitHelper.getApiService())
        val viewModelFactory = StudentPresentlyViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(StudentPresentlyViewModel::class.java)

        viewModel.attendanceSummary.observe(viewLifecycleOwner, Observer { response ->
            // Handle the API response here
            if (response != null && response.status) {
                shwoTotalAttencesStudent(
                    response.data.summary.Present,
                    response.data.summary.Absent,
                    response.data.summary.Leaves
                )
                shwoMonthAttencesStudent(
                    response.data.monthly_details.Present,
                    response.data.monthly_details.Absent,
                    response.data.monthly_details.Leaves
                )
                checkTodayYesterday(
                    response.data.today_status.today_status,
                    response.data.yesterday_status.yesterday_status,
                )


                toolbox.circlePrint(
                    MethodLibrary().formatPercentage(response.data.summary.Present_Percentage.removeSuffix("%")),
                    MethodLibrary().formatPercentage(response.data.summary.Absent_Percentage.removeSuffix("%")),
                    MethodLibrary().formatPercentage(response.data.summary.Leave_Percentage.removeSuffix("%")),
                    circularProgressViewPresent,
                    circularProgressViewAbsent,
                    circularProgressViewLeave,
                    requireContext()
                )
            } else {
                // Handle the failure case (response is null or status is false)
                Toast.makeText(requireContext(), "Failed: Something went wrong", Toast.LENGTH_LONG).show()
            }
        })
        // Fetch attendance summary
//        Toast.makeText(requireContext(), SchoolId().getRollNo(requireContext()), Toast.LENGTH_SHORT).show()
        viewModel.fetchAttendanceSummary(
            SchoolId().getSchoolId(requireContext()),
            StudentInfo().getStudentId(requireContext())
        )
    }

    private fun shwoTotalAttencesStudent(
        totalPresent: String,
        totalAbsent: String,
        totalLeave: String,
    ) {

        binding.tvTotalPresent.setText(totalPresent)
        binding.tvTotalAbsent.setText(totalAbsent)
        binding.tvTotalLeave.setText(totalLeave)
    }


    private fun shwoMonthAttencesStudent(
        totalPresent: String,
        totalAbsent: String,
        totalLeave: String,
    ) {
        binding.tvMonthPresent.setText(totalPresent)
        binding.tvMonthAbsent.setText(totalAbsent)
        binding.tvMonthLeave.setText(totalLeave)
    }


    private fun checkTodayYesterday(
        todayPresenty:String,
        YesterdayPresenty:String,
    ){
        fun todayAttendancePresent() {
            binding.tvTodayPresenty.setText("Present")
            binding.tvTodayPresenty.setTextColor(getResources().getColor(R.color.green))
        }

        fun todayAttendanceAbsent() {
            binding.tvTodayPresenty.setText("Absent")
            binding.tvTodayPresenty.setTextColor(getResources().getColor(R.color.red))
        }

        fun YesterdayAttendancePresent() {
            binding.tvYesterdayPresenty.setText("Present")
            binding.tvYesterdayPresenty.setTextColor(getResources().getColor(R.color.green))
        }

        fun YesterdaytendanceAbsent() {
            binding.tvYesterdayPresenty.setText("Absent")
            binding.tvYesterdayPresenty.setTextColor(getResources().getColor(R.color.red))
        }

        fun todayAttendanceLeave() {
            binding.tvTodayPresenty.setText("Leave")
            binding.tvTodayPresenty.setTextColor(getResources().getColor(R.color.AttencesLeave))
        }

        fun YesterdayAttendanceLeave() {
            binding.tvYesterdayPresenty.setText("Leave")
            binding.tvYesterdayPresenty.setTextColor(getResources().getColor(R.color.AttencesLeave))
        }

        when(todayPresenty){
            "A" -> todayAttendanceAbsent()
            "P" -> todayAttendancePresent()
            "L" -> todayAttendanceLeave()
        }

        when(YesterdayPresenty){
            "A" -> YesterdaytendanceAbsent()
            "P" -> YesterdayAttendancePresent()
            "L" -> YesterdayAttendanceLeave()
        }
    }

    private fun checkTodayYesterdayPrincipal(
        todayPresenty:String,
        YesterdayPresenty:String,
    ){
        fun todayAttendancePresent() {
            binding.tvTodayPresenty.setText("Present")
            binding.tvTodayPresenty.setTextColor(getResources().getColor(R.color.green))
        }

        fun todayAttendanceAbsent() {
            binding.tvTodayPresentyPrincipal.setText("Absent")
            binding.tvTodayPresentyPrincipal.setTextColor(getResources().getColor(R.color.red))
        }

        fun YesterdayAttendancePresent() {
            binding.tvYesterdayPresentyPrincipal.setText("Present")
            binding.tvYesterdayPresentyPrincipal.setTextColor(getResources().getColor(R.color.green))
        }

        fun YesterdaytendanceAbsent() {
            binding.tvYesterdayPresentyPrincipal.setText("Absent")
            binding.tvYesterdayPresentyPrincipal.setTextColor(getResources().getColor(R.color.red))
        }

        fun todayAttendanceLeave() {
            binding.tvTodayPresentyPrincipal.setText("Leave")
            binding.tvTodayPresentyPrincipal.setTextColor(getResources().getColor(R.color.AttencesLeave))
        }

        fun YesterdayAttendanceLeave() {
            binding.tvYesterdayPresentyPrincipal.setText("Leave")
            binding.tvYesterdayPresentyPrincipal.setTextColor(getResources().getColor(R.color.AttencesLeave))
        }

        when(todayPresenty){
            "A" -> todayAttendanceAbsent()
            "P" -> todayAttendancePresent()
            "L" -> todayAttendanceLeave()
        }

        when(YesterdayPresenty){
            "A" -> YesterdaytendanceAbsent()
            "P" -> YesterdayAttendancePresent()
            "L" -> YesterdayAttendanceLeave()
        }
    }

    fun showActiveDeactiveStatus(){
//        Glide.with(this)
//            .load(R.drawable.greendot)
//            .transform(CircleCrop())
//            .into(binding.imgStatusShow)
        toolbox.displayImage(R.drawable.greendot,binding.imgStatusShow, requireContext())
    }

    private fun animationLinearLayout(
        linearLayout: View,
        startLayoutHeight: Int,
        targetLayoutHeight: Int,
        timeDuration: Long
    ) {
        val startHeight = linearLayout.height
        val targetHeight: Int

        // Toggle between expanded and collapsed states
        if (isExpanded) {
            targetHeight = (resources.displayMetrics.density * startLayoutHeight).toInt()
            binding.showmypersonalinfo.text = "Show My Personal Information"
        } else {
            targetHeight = (resources.displayMetrics.density * targetLayoutHeight).toInt()
            binding.showmypersonalinfo.text = "Hide My persional infromation"
        }

        val animator = ValueAnimator.ofInt(startHeight, targetHeight)
        animator.duration = timeDuration
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val params = linearLayout.layoutParams
            params.height = value
            linearLayout.layoutParams = params
        }
        animator.start()
        isExpanded = !isExpanded
    }
    private fun animationLinearLayoutpriciple(
        linearLayout: View,
        startLayoutHeight: Int,
        targetLayoutHeight: Int,
        timeDuration: Long
    ) {
        val targetHeight: Int
        val arrowDrawable: Int

        // Toggle between expanded and collapsed states
        if (isExpanded) {
            targetHeight = (resources.displayMetrics.density * startLayoutHeight).toInt()
            binding.showmypersonalinfo.text = "Show My Personal Information"
            arrowDrawable = R.drawable.arrow_circle_down_svgrepo_com
        } else {
            targetHeight = (resources.displayMetrics.density * targetLayoutHeight).toInt()
            binding.showmypersonalinfo.text = "Hide My Personal Information"
            arrowDrawable = R.drawable.arrow_circle_up_svgrepo_com
        }

        binding.arrow.setImageResource(arrowDrawable)

        val animator = ValueAnimator.ofInt(linearLayout.height, targetHeight)
        animator.duration = timeDuration
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val params = linearLayout.layoutParams
            params.height = value
            linearLayout.layoutParams = params
        }
        animator.start()
        isExpanded = !isExpanded
    }
    private fun observeHomeworkList() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetHomeworkRepository(apiService)
        val factory = GetHomeworkViewModelFactory(repository)
        viewModelGetHomework = ViewModelProvider(this, factory)[GetHomeworkViewModel::class.java]
        viewModelGetHomework.getHomeWork(SchoolId().getSchoolId(requireContext()), "")

        viewModelGetHomework.homeworkList.observe(viewLifecycleOwner) { getHomeworkData ->
            if (!getHomeworkData.isNullOrEmpty()) {
                val homeworkCount = getHomeworkData.size
                val count = StudentInfo().getHomeworkItemCount(requireContext()).toString()
                if(count.toInt() != homeworkCount){
                    showActiveDeactiveStatus()
                }else{
                    resizeIndicatorImage(15,15)
                }
            } else {
              //  Toast.makeText(requireContext(), "Failed to load homework list", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resizeIndicatorImage(
        widthInDp:Int,
        heightInDp:Int
    ){
        val imgStatusShow: ImageView = binding.imgStatusShow

        val density = resources.displayMetrics.density
        val widthInDp = widthInDp
        val heightInDp = heightInDp

        val widthInPixels = (widthInDp * density).toInt()
        val heightInPixels = (heightInDp * density).toInt()

        val params = imgStatusShow.layoutParams

        params.width = widthInPixels
        params.height = heightInPixels

        imgStatusShow.layoutParams = params
    }


    private fun getEmployeeAttence(){
        val schoolId = SchoolId().getSchoolId(requireContext())
        val studentId = teacherDetails.getTeacherId(requireContext())

        employeesPresentlyViewModel.fetchEmployeeSummary(schoolId, studentId)
        employeesPresentlyViewModel.employeeSummary.observe(viewLifecycleOwner) { employeeSummary ->
            employeesPresentlyViewModel.employeeSummary.observe(viewLifecycleOwner, Observer { response ->
                if (response != null && response.status) {

                    if (SchoolId().getLoginRole(requireContext()).equals("Principal")){

                        MethodLibrary().circlePrint(
                            MethodLibrary().formatPercentage(response.data.summary.Present_Percentage.removeSuffix("%")),
                            MethodLibrary().formatPercentage(response.data.summary.Absent_Percentage.removeSuffix("%")),
                            MethodLibrary().formatPercentage(response.data.summary.Leave_Percentage.removeSuffix("%")),
                            circularProgressViewPresentPricipal,
                            circularProgressViewAbsentPricipal,
                            circularProgressViewLeavePricipal,
                            requireContext()
                        )
                        // Show Over All Present Principal
                        toolbox.showSetText(response.data.summary.Present, binding.tvDayPresentCountPrinciapal)
                        toolbox.showSetText(response.data.summary.Absent, binding.tvDayAbsentCountPrinciapal)
                        toolbox.showSetText(response.data.summary.Leaves, binding.tvDayLeaveCountPrinciapal)

                        // Show Month Present Principal
                        toolbox.showSetText(response.data.monthly_details.Present, binding.tvMonthPresentCountPrincipal)
                        toolbox.showSetText(response.data.monthly_details.Absent, binding.tvMonthAbsentCountPrincipal)
                        toolbox.showSetText(response.data.monthly_details.Leaves, binding.tvMonthLeaveCountPrincipal)

                        // Show Today And Yesterday Status Principal
//                        checkTodayYesterdayPrincipal(
//                            response.data.yesterday_status.yesterday_status,
//                            response.data.today_status.today_status)

                        // show Today and Yesterday Present Status Emp
                        toolbox.checkPresentStatus(
                            response.data.today_status.today_status,
                            response.data.yesterday_status.yesterday_status,
                            binding.tvYesterdayPresentyPrincipal,
                            binding.tvTodayPresentyPrincipal,
                            requireContext())
                    }else{
                        // show Present Record Emp
                        toolbox.showSetText(response.data.summary.Absent , binding.tvTotalAbsent)
                        toolbox.showSetText(response.data.summary.Present , binding.tvTotalPresent)
                        toolbox.showSetText(response.data.summary.Leaves , binding.tvTotalLeave)

                        // show Today and Yesterday Present Status Emp
                        toolbox.checkPresentStatus(
                            response.data.today_status.today_status,
                            response.data.yesterday_status.yesterday_status,
                            binding.tvTodayPresenty,
                            binding.tvYesterdayPresenty,
                            requireContext())

                        // show months Present Record Emp
                        toolbox.showSetText(response.data.monthly_details.Present , binding.tvMonthPresent)
                        toolbox.showSetText(response.data.monthly_details.Absent , binding.tvMonthAbsent)
                        toolbox.showSetText(response.data.monthly_details.Leaves , binding.tvMonthLeave)


                        MethodLibrary().circlePrint(
                            toolbox.formatPercentage(response.data.summary.Present_Percentage.removeSuffix("%")),
                            toolbox.formatPercentage(response.data.summary.Absent_Percentage.removeSuffix("%")),
                            toolbox.formatPercentage(response.data.summary.Leave_Percentage.removeSuffix("%")),
                            circularProgressViewPresent,
                            circularProgressViewAbsent,
                            circularProgressViewLeave,
                            requireContext()
                        )
                    }


                } else {
                    // Handle the failure case (response is null or status is false)
                    Toast.makeText(requireContext(), "Failed: Something went wrong", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun isEmployeeActive(){
        // Observing LiveData for student status
        employeeViewModel.employeeStatus.observe(requireActivity(), Observer { response ->
            // Handle the successful response
            response?.let {
                // Update your UI with the response data
                Toast.makeText(requireContext(), it.status, Toast.LENGTH_SHORT).show()
            }
        })
        // Observing LiveData for error messages
        employeeViewModel.errorMessage.observe(requireActivity(), Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), "Massage Not available", Toast.LENGTH_SHORT).show()
            }
        })
        employeeViewModel.getEmployeeStatus(requireContext(),SchoolId().getSchoolId(requireContext()),TeacherDetails().getEmployeeID(requireContext()))
    }

    private fun isStudentActive(){
        // Observing LiveData for student status
        studentViewModel.studentStatus.observe(requireActivity(), Observer { response ->
            // Handle the successful response
            response?.let {
                // Update your UI with the response data
                Toast.makeText(requireContext(), it.status, Toast.LENGTH_SHORT).show()
            }
        })

        // Observing LiveData for error messages
        studentViewModel.errorMessage.observe(requireActivity(), Observer { errorMessage ->
            // Handle error messages
            errorMessage?.let {
                Toast.makeText(requireContext(), "Massage Not available", Toast.LENGTH_SHORT).show()
            }
        })
        studentViewModel.getStudentStatus(requireContext(),SchoolId().getSchoolId(requireContext()),StudentInfo().getStudentId(requireContext()))
    }
}
