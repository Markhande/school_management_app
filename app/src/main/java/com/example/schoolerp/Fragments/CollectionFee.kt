package com.example.schoolerp.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.example.schoolerp.Adapter.GetFeesStudentNameAdapter
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.FeeParticularData
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.Fragments.Fragment.AddStudent
import com.example.schoolerp.Fragments.Fragment.FeeParticular
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentCollectionFeeBinding
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.DateWiseStudentAttendanceRepository
import com.example.schoolerp.repository.GetFeeParticularRepository
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.DateWiseStudentAttendanceViewModel
import com.example.schoolerp.viewmodel.GetFeeParticularViewModel
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.DateWiseStudentAttendanceViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetFeeParticularViewModelFactory


class CollectionFee : Fragment(), GetFeesStudentNameAdapter.OnItemClickListener {
    private lateinit var binding: FragmentCollectionFeeBinding
    private lateinit var viewModelAllclass: AllClassViewModel
    private var selectedClassName: String? = null
    private lateinit var viewmodelDateWise: DateWiseStudentAttendanceViewModel
    private lateinit var connectivityReceiver: BroadcastReceiver

    private lateinit var viewModel: GetFeeParticularViewModel
    private lateinit var adapter: GetFeesStudentNameAdapter
    private lateinit var getfeesList: List<FeeParticularData>
    val toolBox = MethodLibrary()
    private var isDataObserved = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCollectionFeeBinding.bind(
            inflater.inflate(
                R.layout.fragment_collection_fee,
                null
            )
        )
        setupviewmodelallclass()
        getSchoolId()
        observeDataAllClass()
        setupViewModelAllClass()
        initView()
        observeDataAllName()
        setupViewgetfees()
        registerConnectivityReceiver()
        requireContext().registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        return binding.root
    }

    private fun registerConnectivityReceiver() {
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let {
                    if (isInternetAvailable(it)) {
                        observeDataAllClass()
                    }
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(connectivityReceiver, intentFilter)
    }


    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    override fun onDestroyView() {
        super.onDestroyView()

        // Unregister the receiver only if it has been initialized
        if (::connectivityReceiver.isInitialized) {
            requireContext().unregisterReceiver(connectivityReceiver)
        }
    }

    private fun setupViewModelAllClass() {
        val apiService = RetrofitHelper.getApiService()
        val repository = DateWiseStudentAttendanceRepository(apiService)
        val factory = DateWiseStudentAttendanceViewModelFactory(repository)
        viewmodelDateWise =
            ViewModelProvider(this, factory).get(DateWiseStudentAttendanceViewModel::class.java)
    }

    private fun setupViewgetfees() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetFeeParticularRepository(apiService)
        val factory = GetFeeParticularViewModelFactory(repository)
        viewModel =
            ViewModelProvider(this, factory).get(GetFeeParticularViewModel::class.java)
    }

    private fun getSchoolId(): String {
        val sharedPreferences = requireActivity().getSharedPreferences(
            "onboarding_prefs", AppCompatActivity.MODE_PRIVATE
        )
        return sharedPreferences.getString("school_id", "").orEmpty()
    }


    private fun setupviewmodelallclass() {
        val repository = AllClassRepository()
        val factory = AllClassViewModelFactory(repository)
        viewModelAllclass = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)
    }

    private fun observeDataAllClass() {
        if (isDataObserved) return

        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
            return
        }

        isDataObserved = true // Mark as observed

        val schoolId = getSchoolId() ?: run {
            Log.e("MarksStudentAttendance", "School ID is null or blank. Cannot fetch data.")
            return
        }

        viewModelAllclass.getClasses(schoolId.trim())
            .observe(viewLifecycleOwner, Observer { response ->
                if (response != null && response.status) {
                    Log.d(
                        "MarksStudentAttendance",
                        "Classes fetched successfully: ${response.data}"
                    )

                    val classNames = response.data.map { it.class_name }
                    val classIds = response.data.map { it.class_id }

                    setupSpinner(classNames, classIds)
                } else {
                    showAddClassDialog()
                    Log.e(
                        "MarksStudentAttendance",
                        "Failed to fetch class data or no data available."
                    )
                }
            })
    }

    private fun setupSpinner(classNames: List<String>, classIds: List<String>) {
        if (classNames.isEmpty()) {
            Log.d("MarksStudentAttendance", "No classes found.")
            binding.AllClassName.isEnabled = false
            return
        }

        val classNameAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classNames)
        classNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.AllClassName.adapter = classNameAdapter

        binding.AllClassName.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedClassName = classIds[position]
                    Log.d("MarksStudentAttendance", "Selected class: $selectedClassName")
                    fetchAttendanceData()
                    val selectedClassName = parentView.getItemAtPosition(position) as String

                    val selectedIndex = classNames.indexOf(selectedClassName)

                    if (selectedIndex >= 0) {
                        val selectedClassId = classIds[selectedIndex]

                        val updatedAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            listOf(selectedClassId)
                        )
                        updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.onlyId.adapter = updatedAdapter
                    } else {
                        binding.onlyId.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            listOf("Select Class ID")
                        )
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    binding.onlyId.adapter = ArrayAdapter(
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
            title = "Classes Not Available.",
            message = "Would you like to add a class?",
            positiveButtonText = "Yes",
            positiveButtonAction = { toolBox.fragmentChanger(NewClass(), requireContext()) },
            negativeButtonText = "No",
            negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
    }


    private fun navigateToAddClassScreen() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, NewClass())
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun observeDataAllName() {
        viewmodelDateWise.attendanceData.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status && response.data.students.isNotEmpty()) {
                val studentNames = response.data.students
                toggleEmptyView(false)
                adapter.updateList(studentNames)
            } else {
                Log.e("MarksStudentAttendance", "Failed to fetch attendance data.")
                toggleEmptyView(true)
            }
        }
    }

    private fun toggleEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.visibility = View.VISIBLE
            binding.recyclerViewFeesStudentNames.visibility = View.GONE
        } else {
            // Show the RecyclerView and hide the "No data" text
            binding.txtForDefault.visibility = View.GONE
            binding.recyclerViewFeesStudentNames.visibility = View.VISIBLE
        }

        // Set up the listener to navigate to the AddStudent fragment when the "No data" text is clicked
        setuplistner(isEmpty)
    }

    private fun setuplistner(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.setOnClickListener {
                // Replace the current fragment with AddStudentFragment when the text is clicked
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(
                    R.id.fragment_container,
                    AddStudent()
                )  // Replace with your container ID
                transaction.addToBackStack(null)  // Add the transaction to back stack if you want to navigate back
                transaction.commit()

                // Optionally hide the "No data" text and show the RecyclerView once the fragment is replaced
                binding.txtForDefault.visibility = View.GONE
                binding.recyclerViewFeesStudentNames.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchAttendanceData() {
        val schoolId = getSchoolId()?.trim()
        val classId = selectedClassName?.trim()

        if (!schoolId.isNullOrEmpty() && !classId.isNullOrEmpty()) {
            viewmodelDateWise.fetchAttendance(schoolId, classId)
            Log.d(
                "MarksStudentAttendance",
                "Fetching data for school ID: $schoolId, class: $classId"
            )

            viewmodelDateWise.attendanceData.observe(viewLifecycleOwner) { response ->
                if (response != null && response.status) {
                    val studentNames = response.data.students
                    if (!studentNames.isNullOrEmpty()) {
                        Log.d(
                            "MarksStudentAttendance",
                            "Attendance data fetched successfully for school ID: $schoolId, class: $classId"
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No Student data found for the selected class.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("MarksStudentAttendance", "No attendance data available.")
                    }
                }
            }
        }
    }

    private fun initView() {
        adapter = GetFeesStudentNameAdapter(
            emptyList(),
            object : GetFeesStudentNameAdapter.OnItemClickListener {
                override fun onItemClick(student: Student) {
                    fetchAttendanceData(student)
                }
            })

        binding.recyclerViewFeesStudentNames.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CollectionFee.adapter
        }
    }


    override fun onItemClick(student: Student) {
        fetchAttendanceData(student)
    }

    private fun fetchAttendanceData(student: Student) {
        val schoolId = getSchoolId().trim()

        if (!schoolId.isNullOrEmpty() && student.id.isNotEmpty()) {
            // Fetch fee particulars from the API
            viewModel.fetchFeeParticulars(schoolId, student.id)
                .observe(viewLifecycleOwner) { response ->
                    if (response != null && response.status) {
                        // Pass the fee data to the fragment
                        navigateToDetailFragment(response.data)
                    } else {
                        showAddFeeParticularDialog()
                        Log.e("CollectionFee", "Failed to fetch data or no data available.")
                        Toast.makeText(
                            context,
                            "No data available for this student.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Log.e("CollectionFee", "Invalid school ID or student ID.")
        }
    }

    private fun navigateToDetailFragment(feeData: List<FeeParticularData>) {
        val bundle = Bundle().apply {
            putParcelableArrayList(
                "Fees",
                ArrayList(feeData) // Passing a list of FeeParticularData
            )
        }

        val feesDetailStudentFragment = FeesCollectionDetailFragment().apply {
            arguments = bundle
        }

        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, feesDetailStudentFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showAddFeeParticularDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Data Not Available.",
            message = "Would you like to add particular fees?",
            positiveButtonText = "Yes",
            positiveButtonAction = { toolBox.fragmentChanger(FeeParticular(), requireContext()) },
            negativeButtonText = "No",
            negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = true
        )
    }
}

/*  // Create an AlertDialog to ask if the user wants to add fee particulars
  AlertDialog.Builder(requireContext())
      .setTitle("No Fee Particulars Available")
      .setMessage("There are no fee particulars available for this student. Would you like to add them?")
      .setPositiveButton("Yes") { dialog, _ ->
          // Navigate to the Add Fee Particulars screen
          navigateToAddFeeParticularScreen()
          dialog.dismiss()
      }
      .setNegativeButton("No") { dialog, _ ->
          dialog.dismiss()
      }
      .show()
}
private fun navigateToAddFeeParticularScreen() {
  // Navigate to the Add Fee Particulars screen
  val transaction = requireActivity().supportFragmentManager.beginTransaction()
  transaction.replace(R.id.fragment_container, FeeParticular())  // Replace with your Add Fee Particulars fragment
  transaction.addToBackStack(null)  // Add the transaction to the back stack if you want to navigate back
  transaction.commit()
}
*/
