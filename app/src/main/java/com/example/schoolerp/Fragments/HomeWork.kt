package com.example.schoolerp.Fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.HomeworkAdapter
import com.example.schoolerp.Adapter.HomeworkHistoryAdapter
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.Homework
import com.example.schoolerp.Fragments.Fragment.AddStudent
import com.example.schoolerp.Fragments.Fragment.FeeParticular
import com.example.schoolerp.Fragments.SendMessageAdmin.Companion.REQUEST_IMAGE_CAPTURE_1
import com.example.schoolerp.Fragments.SendMessageAdmin.Companion.REQUEST_IMAGE_GALLERY_1
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentHomeWorkBinding
import com.example.schoolerp.repository.AddHomeworkRepository
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.AllEmployeesRepository
import com.example.schoolerp.repository.ClassWiseSubjectRepository
import com.example.schoolerp.repository.EmployeeAndPrincipleRepository
import com.example.schoolerp.repository.GetHomeworkRepository
import com.example.schoolerp.repository.SearchHistoryHomeworkRepository
import com.example.schoolerp.repository.getStudentRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AddHomeworkViewModel
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.AllEmployeesViewModel
import com.example.schoolerp.viewmodel.ClassWiseSubjectViewModel
import com.example.schoolerp.viewmodel.EmployeeAndPrincipleViewModel
import com.example.schoolerp.viewmodel.GetHomeworkViewModel
import com.example.schoolerp.viewmodel.SearchHistoryHomeworkViewModel
import com.example.schoolerp.viewmodel.getStudentViewModel
import com.example.schoolerp.viewmodelfactory.AddHomeworkViewModelFactoryclass
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.AllEmployeesViewModelFactory
import com.example.schoolerp.viewmodelfactory.ClassWiseSubjectViewModelFactory
import com.example.schoolerp.viewmodelfactory.EmployeeAndPrincipleViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetHomeworkViewModelFactory
import com.example.schoolerp.viewmodelfactory.SearchHistoryHomeworkViewModelFactory
import com.example.schoolerp.viewmodelfactory.getStudentViewModelFactory
import com.example.student.FullScreenImageActivity
import com.example.teacher.TeacherDetails
import org.w3c.dom.Text
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeWork : Fragment() {
    private lateinit var binding: FragmentHomeWorkBinding
    private val homeworkList = mutableListOf<Homework>()
    private var selectedImageUri: Uri? = null
    private lateinit var adapter: HomeworkAdapter
    private lateinit var viewModelGetHomework: GetHomeworkViewModel
    private lateinit var viewModelAllEmp: AllEmployeesViewModel
    private lateinit var viewModelAddHome: AddHomeworkViewModel
    private lateinit var imageAttched: ImageView

    private lateinit var imageView: ImageView

    private var isDialogVisible = false
    private var isExpand = true // Start with collapsed state
    val toolBox = MethodLibrary()

    private lateinit var viewmodelAllClass: AllClassViewModel
    private lateinit var viewModelCalssWise: ClassWiseSubjectViewModel
    private lateinit var viewModelgetTeacher: EmployeeAndPrincipleViewModel
    private lateinit var viewModelSearchHistory: SearchHistoryHomeworkViewModel
    private lateinit var HomeworkHistoryAdapter: HomeworkHistoryAdapter
    var photo1: Bitmap? = null
    var photo2: Bitmap? = null
    private val PERMISSION_REQUEST_CODE = 103

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200

    private var currentPhotoPath: String? = null

    companion object {
        const val REQUEST_IMAGE_CAPTURE_1: Int = 1
        const val REQUEST_IMAGE_CAPTURE_2: Int = 2

        const val REQUEST_IMAGE_GALLERY_1: Int = 11
        const val REQUEST_IMAGE_GALLERY_2: Int = 12

    }

    private var dialogView: View? = null
    private var dialogView1: View? = null

    private var photoFile: File? = null

    private lateinit var connectivityReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeWorkBinding.inflate(inflater, container, false)
        initView()
        setUpListeners()
        // observalAllEmpSpinner()
        viewModelGetHomeWork()
        getSchoolId()
        fetchdata()
        observeHomeworkList()
        viewModelGetEmp()
        viewModelAddHomeWork()
        setupViewModelClassNameStudent()

        modelviewgetteacher()
        setupObserver()
        setupViewModeClassWise()
        viewmodelSearchHomeworkHostory()
        setuplistnersSearchHistery()
        viewModelAllClass()
        setupAllClassObserver()

        registerConnectivityReceiver()


//        binding.imgRemoveLayout.setImageResource(R.drawable.arrowdownhome)
//        binding.imgRemoveLayout.setOnClickListener {
//
//            isExpand = !isExpand
//            MethodLibrary().toggleLayoutExpansion(
//                AnimationLayout = binding.AddHomeworkLayout,
//                PrimaryImage = binding.imgRemoveLayout,
//                PrimaryImageResource = R.drawable.arrowdownhome,
//                SecondaryImage = binding.imgRemoveLayout,
//                SecondaryImageResource = R.drawable.arrowuphome,
//                startingHeight = 130,
//                EndingHeight = 500,
//                Timing = 500L,
//                DefaultValue = isExpand,
//                requireContext()
//            )
//        }

        setuplisners()

        binding.txtSearchHomework.setOnClickListener{
            isExpand = !isExpand
            MethodLibrary().toggleLayoutExpansion(
                AnimationLayout = binding.layoutSearchHomework,
                PrimaryText = binding.txtSearchHomework,
                SecondText = binding.txtSearchHomework,
                startingHeight = 0,
                EndingHeight = 340,
                Timing = 500,
                defaultValue = isExpand,
                requireContext()
            )
        }

        toolBox.clicked(binding.refreshImageLayout){
            initView()
            setUpListeners()
            // observalAllEmpSpinner()
            viewModelGetHomeWork()
            getSchoolId()
            fetchdata()
            observeHomeworkList()
            viewModelGetEmp()
            viewModelAddHomeWork()
            setupViewModelClassNameStudent()

            modelviewgetteacher()
            setupObserver()
            setupViewModeClassWise()
            viewmodelSearchHomeworkHostory()
            setuplistnersSearchHistery()
            viewModelAllClass()
            setupAllClassObserver()

            registerConnectivityReceiver()

            toolBox.rotateImage(
                imageView =  binding.refreshImage,
                duration = 1000
            )       }

        return binding.root
    }
    private fun registerConnectivityReceiver() {
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let {
                    if (isInternetAvailable(it)) {
                        fetchdata()
                        observeHomeworkList()
                        initView()
                    }
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(connectivityReceiver, intentFilter)
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
            requireContext().unregisterReceiver(connectivityReceiver)
        }catch (e : Exception){
            //Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    private fun setupListners(){
        /*  binding.txtRefresh.setOnClickListener{
              fetchdata()
              observeHomeworkList()
              initView()
              searchHistoryObserval()
          }*/
    }

    private fun setuplisners(){

        binding.edtDateCurrent.setOnClickListener {
            showDatePickerDialog()

        }

    }

    private fun viewmodelSearchHomeworkHostory() {
        val apiService = RetrofitHelper.getApiService()
        val repository = SearchHistoryHomeworkRepository(apiService)
        val factory = SearchHistoryHomeworkViewModelFactory(repository)
        viewModelSearchHistory =
            ViewModelProvider(this, factory).get(SearchHistoryHomeworkViewModel::class.java)

    }

    private fun searchHomeWork() {
        if (binding.spStudentSelectClassIdAddHomeWork.selectedItem.toString()
                .isNotEmpty() && !binding.spStudentSelectClassIdAddHomeWork.selectedItem.toString()
                .equals(null)
        ) {

            val homeworkHistoryMap = mapOf(
                "homework_date" to binding.edtDateCurrent.editText?.text.toString(),
                "set_by" to binding.TeacherAndPrincile.selectedItem.toString(),
                "class" to binding.spStudentSelectClassIdAddHomeWork.selectedItem.toString(),
                "school_id" to SchoolId().getSchoolId(requireContext())
            )
            Log.d("SearchHomeWork", "Homework data as Map: $homeworkHistoryMap")

            viewModelSearchHistory.searchHomeworkHistory(homeworkHistoryMap)
            searchHistoryObserval()
        } else {
            Toast.makeText(requireContext(), "Please Enter Date", Toast.LENGTH_SHORT).show()
        }

    }

    private fun searchHistoryObserval() {
        Log.d("SearchHistory", "Observing homework history...")

        // Initialize adapter and set it to RecyclerView
        val adapter = HomeworkHistoryAdapter(emptyList())
        binding.recyclerViewHomeworkHistory.adapter = adapter
        binding.recyclerViewHomeworkHistory.layoutManager = LinearLayoutManager(requireContext())

        // Initially hide RecyclerView and show progress bar
        binding.recyclerViewHomeworkHistory.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        // Observe homework history
        viewModelSearchHistory.homeworkHistory.observe(viewLifecycleOwner) { homeworkList ->
            Log.d("HomeworkHistory", "Number of homework items: ${homeworkList?.size ?: 0}")

            if (!homeworkList.isNullOrEmpty()) {
                // If data exists, update adapter and show RecyclerView
                adapter.updateData(homeworkList)
                binding.recyclerViewHomeworkHistory.visibility = View.VISIBLE
                binding.RecyclerViewAddHomeWork.visibility = View.GONE
                //toggleEmptyView1(true)


            } else {
                // If no data, clear the adapter and show a message
                //toggleEmptyView1(false)
                adapter.updateData(emptyList())
                binding.recyclerViewHomeworkHistory.visibility = View.GONE
                Toast.makeText(requireContext(), "No homework history found", Toast.LENGTH_SHORT)
                    .show()
            }

            // Hide the progress bar in both cases
            binding.progressBar.visibility = View.GONE
        }

        // Observe loading state (optional)
        viewModelSearchHistory.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors
        viewModelSearchHistory.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun toggleEmptyView1(isEmpty: Boolean) {
        if (isEmpty) {
            // Show the "No data" text and hide the RecyclerView
            binding.txtForDefault.visibility = View.VISIBLE
            binding.recyclerViewHomeworkHistory.visibility = View.GONE
        } else {
            // Show the RecyclerView and hide the "No data" text
            binding.txtForDefault.visibility = View.GONE
            binding.recyclerViewHomeworkHistory.visibility = View.VISIBLE
        }

        //  setuplistner(isEmpty)
    }

    private fun setuplistnersSearchHistery() {
        binding.btnSearchHomeWorkHistory.setOnClickListener {
            // Hide the recycler view and show the progress bar
            binding.recyclerViewHomeworkHistory.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            // Trigger search without checking the class selection
            searchHomeWork()
        }
        val calendar = Calendar.getInstance()
        val currentDate = "${calendar.get(Calendar.DAY_OF_MONTH).padToTwoDigits()}/" +
                "${(calendar.get(Calendar.MONTH) + 1).padToTwoDigits()}/" +
                "${calendar.get(Calendar.YEAR)}"

        binding.edtDateCurrent.editText?.setText(currentDate) // Set formatted date in TextInputEditText

        // Show DatePickerDialog when the date field is clicked
        binding.edtDateCurrent.editText?.setOnClickListener {
            //Log.d("DateClickListener", "EditText clicked for date selection")

            showDatePickerDialog()
        }
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Format the selected date to dd/MM/yyyy
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val formattedDate = getFormattedDate(selectedDate.time)
                binding.edtDateCurrent.editText?.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Prevent multiple rapid clicks by disabling and enabling the dialog trigger
        binding.edtDateCurrent.editText?.isEnabled = false
        datePickerDialog.setOnDismissListener {
            binding.edtDateCurrent.editText?.isEnabled = true
        }

        // Show the DatePickerDialog
        datePickerDialog.show()
    }

    // Helper function to format the date to dd/MM/yyyy
    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    // Extension function to pad numbers to two digits
    private fun populateSpinner(employeeNames: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, employeeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.TeacherAndPrincile.adapter = adapter

        binding.TeacherAndPrincile.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedName = employeeNames[position]
                // Handle selection if needed
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    //  ---------------Start Calling class API to show all class data in spinner ----------
    private fun viewModelAllClass(){
        val factory = AllClassViewModelFactory(AllClassRepository())
        viewmodelAllClass = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)

    }

    private fun setupAllClassObserver() {
        // Observe the LiveData from ViewModel
        viewmodelAllClass.getClasses(SchoolId().getSchoolId(requireContext())).observe(viewLifecycleOwner) { response ->
            response?.data?.let { classList ->
                if (classList.isNotEmpty()) {
                    // Extract the class names and class IDs from the classList
                    val classNames = mutableListOf("Select Class") // Add "Select Class" as the default item
                    val classIds = mutableListOf("Select Class ID") // Add "Select Class ID" as the default item

                    // Add class names and class IDs from the class list
                    classNames.addAll(classList.map { it.class_name })
                    classIds.addAll(classList.map { it.class_id.toString() })

                    // Create adapter for the first spinner with class names (including "Select Class")
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    // Create adapter for the second spinner with class IDs (including "Select Class ID")
                    val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classIds)
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    // Set the adapters to the respective Spinners
                    binding.spStudentSelectClass.adapter = adapter
                    binding.spStudentSelectClassIdAddHomeWork.adapter = adapter2

                    // Set the OnItemSelectedListener for the first Spinner
                    binding.spStudentSelectClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                            // Get the selected class name (from first spinner)
                            val selectedClassName = parentView.getItemAtPosition(position) as String

                            // Filter class IDs based on the selected class name
                            val filteredClassIds = classList.filter { it.class_name == selectedClassName }
                                .map { it.class_id.toString() }

                            // Ensure filteredClassIds is not empty before updating the second spinner
                            if (filteredClassIds.isNotEmpty()) {
                                // Update the second spinner with the filtered class IDs
                                val updatedAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filteredClassIds)
                                updatedAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.spStudentSelectClassIdAddHomeWork.adapter = updatedAdapter2
                            } else {
                                // If no matching class IDs, set the default "Select Class ID"
                                binding.spStudentSelectClassIdAddHomeWork.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Class ID"))
                            }
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>) {
                            // Handle case when nothing is selected (optional)
                            // Set the default "Select Class ID" in the second spinner
                            binding.spStudentSelectClassIdAddHomeWork.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Class ID"))
                        }
                    }
                } else {
                    // If no classes are found, show a Toast
                    Toast.makeText(requireContext(), "No classes found", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                // Handle case when response or data is null
                Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun modelviewgetteacher(){
        val apiService = RetrofitHelper.getApiService()
        val repository = EmployeeAndPrincipleRepository(apiService)
        val factory = EmployeeAndPrincipleViewModelFactory(repository)
        viewModelgetTeacher = ViewModelProvider(this, factory).get(EmployeeAndPrincipleViewModel::class.java)


    }
    private fun setupObserver() {
        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
            return
        }
        val schoolId = getSchoolId() // Replace with your method to get the school ID

        viewModelgetTeacher.fetchTeachersAndPrincipals(schoolId)

        viewModelgetTeacher.employees.observe(viewLifecycleOwner) { teacherList ->
            // Populate Spinner when data is fetched
            val employeeNames = teacherList.map { it.employee_name } // Extract names
            populateSpinner(employeeNames)
        }

        viewModelgetTeacher.error.observe(viewLifecycleOwner) { errorMessage ->
            showTecherDialog()
            // Show error message
            //showToast("Error: $errorMessage")
        }
    }
    private fun showTecherDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Data Not Available",
            message = "",
            positiveButtonText = "Yes",
            positiveButtonAction = {
                toolBox.fragmentChanger(AddNewEmployees(), requireContext()) // Navigate to HomeWork Fragment
            },
            negativeButtonText = "No",
            negativeButtonAction = {
                toolBox.fragmentChanger(DashBoard(), requireContext()) // Navigate to DashBoard Fragment
            },
            cancelable = false // Ensure the dialog cannot be dismissed by tapping outside
        )
    }

    private fun setupViewModeClassWise(){
        val apiService = RetrofitHelper.getApiService() // Initialize Retrofit instance
        val repository = ClassWiseSubjectRepository(apiService)
        val factory = ClassWiseSubjectViewModelFactory(repository)
        viewModelCalssWise = ViewModelProvider(this, factory).get(ClassWiseSubjectViewModel::class.java)

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

    private fun viewModelGetHomeWork() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetHomeworkRepository(apiService)
        val factory = GetHomeworkViewModelFactory(repository)
        viewModelGetHomework =
            ViewModelProvider(this, factory).get(GetHomeworkViewModel::class.java)
    }

    private fun viewModelGetEmp() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AllEmployeesRepository(apiService)
        val factory = AllEmployeesViewModelFactory(repository)
        viewModelAllEmp = ViewModelProvider(this, factory).get(AllEmployeesViewModel::class.java)
    }

    private fun viewModelAddHomeWork() {

        val apiService = RetrofitHelper.getApiService()
        val repository = AddHomeworkRepository(apiService)
        val factory = AddHomeworkViewModelFactoryclass(repository)
        viewModelAddHome = ViewModelProvider(this, factory).get(AddHomeworkViewModel::class.java)
        // viewModelAllEmp.getAllEmployees(schoolId.trim())


    }

    private fun fetchdata() {
        val schoolId = getSchoolId() // Assuming you have a method to get school ID

        viewModelGetHomework.getHomeWork(schoolId.trim(), TeacherDetails().getTeacherId(requireContext()))
    }

    private fun observeHomeworkList() {
        viewModelGetHomework.homeworkList.observe(viewLifecycleOwner) { homeworkList ->
            binding.progressBar.visibility = View.VISIBLE
            binding.RecyclerViewAddHomeWork.visibility = View.GONE

            if (homeworkList != null) {
                binding.progressBar.visibility = View.GONE

                // Default: Show all data
                var filteredList = homeworkList

                // Search button click listener
                binding.btnSearchHomeWorkHistory.setOnClickListener {
                    val dataInput = binding.editTextDate.text.toString().trim()
                    val classInput = binding.spStudentSelectClass.selectedItem.toString().trim()

                    // Filter the list based on input
                    filteredList = homeworkList.filter { homework ->
                        (dataInput.isEmpty() || homework.homework_date.contains(dataInput, ignoreCase = true)) &&
                                (classInput.isEmpty() || homework.class_name.contains(classInput, ignoreCase = true))
                    }

                    // Update the adapter with the filtered list
                    adapter = HomeworkAdapter(filteredList!!, ::onEditClick, ::onDeleteClick)
                    binding.RecyclerViewAddHomeWork.adapter = adapter
                    toggleEmptyView(filteredList!!.isEmpty())
                }

                // Initial setup with all data
                if (filteredList!!.isNotEmpty()) {
                    adapter = HomeworkAdapter(filteredList!!, ::onEditClick, ::onDeleteClick)
                    binding.RecyclerViewAddHomeWork.adapter = adapter
                    toggleEmptyView(false)
                } else {
                    toggleEmptyView(true) // Show default text, hide RecyclerView
                }
            } else {
                binding.progressBar.visibility = View.GONE
                toggleEmptyView(true) // Show default text on null data
                //Toast.makeText(requireContext(), "Failed to load homework list", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private fun toggleEmptyView(showEmptyView: Boolean) {
//        if (showEmptyView) {
//           // binding.textViewEmpty.visibility = View.VISIBLE
//            binding.RecyclerViewAddHomeWork.visibility = View.GONE
//        } else {
//            //binding.textViewEmpty.visibility = View.GONE
//            binding.RecyclerViewAddHomeWork.visibility = View.VISIBLE
//        }
//    }



    private fun initView() {
        adapter = HomeworkAdapter(emptyList(), ::onEditClick, ::onDeleteClick)

        // Set up the LinearLayoutManager with reversed layout
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true // Reverses the order of items
            stackFromEnd = true // Ensures the layout starts from the end
        }

        binding.RecyclerViewAddHomeWork.layoutManager = layoutManager
        binding.RecyclerViewAddHomeWork.adapter = adapter
    }
    private fun toggleEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            // Show the "No data" text and hide the RecyclerView
            binding.txtForDefault.visibility = View.VISIBLE
            binding.RecyclerViewAddHomeWork.visibility = View.GONE
        } else {
            // Show the RecyclerView and hide the "No data" text
            binding.txtForDefault.visibility = View.GONE
            binding.RecyclerViewAddHomeWork.visibility = View.VISIBLE
        }

        setuplistner(isEmpty)
    }
    private fun setuplistner(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.setOnClickListener {
                fetchdata()
                observeHomeworkList()
                initView()
                Toast.makeText(requireContext(), "No data available. Please add the data.", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.txtForDefault.setOnClickListener {
                Toast.makeText(requireContext(), "Data is already available.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
private fun observalAllEmpSpinner(){
    val schoolId = getSchoolId()  // Get the dynamic school_id

    viewModel.getAllEmployees(schoolId.trim()).observe(viewLifecycleOwner) { response ->

    }
*/

    private fun onDeleteClick(homework: Homework) {
        // Step 1: Show a confirmation dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Homework")
            .setMessage("Are you sure you want to delete the homework?")
            .setPositiveButton("Delete") { dialog, _ ->
                dialog.dismiss()

                // Step 2: Get the schoolId and homeworkId for the API call
                val schoolId = homework.school_id.trim() // Get the school ID from the homework object
                val homeworkId = homework.id.toString()  // Get the homework ID

                // Step 3: Find the position of the homework item in the list
                val position = viewModelGetHomework.homeworkList.value?.indexOfFirst {
                    it.id == homework.id && it.school_id == homework.school_id
                } ?: -1

                // Step 4: Check if the homework item exists in the list before attempting to remove it
                if (position != -1) {
                    // Step 5: Show the toast message confirming deletion
                    Toast.makeText(requireContext(), "Deleting homework...", Toast.LENGTH_SHORT).show()

                    // Step 6: Call the delete function in the ViewModel to delete the homework on the server
                    viewModelGetHomework.deleteHomework(schoolId, homeworkId)

                    // Step 7: Observe the result in ViewModel to handle server response
                    viewModelGetHomework.deleteHomeworkResult.observe(viewLifecycleOwner) { response ->
                        response.onSuccess {
                            // Homework deleted successfully on the server
                            Toast.makeText(requireContext(), "Homework deleted successfully", Toast.LENGTH_SHORT).show()

                            // After successfully deleting from the server, remove it from the ViewModel's list
                            viewModelGetHomework.removeHomeworkFromList(homework)
                        }
                        response.onFailure { error ->
                            // Failed to delete homework from the server
                            Toast.makeText(requireContext(), "Failed to delete homework: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Handle case when homework is not found in the list
                    Toast.makeText(
                        requireContext(),
                        "Homework not found. Please refresh the list and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "Deletion cancelled", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }


    private fun setupViewModelClassNameStudent() {
        val repository = AllClassRepository()
        val factory = AllClassViewModelFactory(repository)
        viewmodelAllClass = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)
    }

    private fun setUpListeners() {
        binding.btnAddHomeworks1.setOnClickListener {
            //showAddHomeworkDialog()
            toolBox.fragmentChanger(AddHomework(), requireContext())
        }

    }

    private fun showAddHomeworkDialog() {
        // Initialize dialogView properly
        dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_customhome, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()

        dialogView?.let { view ->
            val txtSearchHomework = view.findViewById<TextView>(R.id.txtSearchHomework)
            val etHomeworkDetail = view.findViewById<EditText>(R.id.edtHomeWorkDetail)
            val etClassName = view.findViewById<Spinner>(R.id.edtClass)
            val etClassId = view.findViewById<TextView>(R.id.edtId)
            val etSubjectName = view.findViewById<Spinner>(R.id.edtSubject)
            val etDate = view.findViewById<EditText>(R.id.edtDateHomeWork)
            val spnSendBy = view.findViewById<Spinner>(R.id.edtSendBy)
            val btnSubmitHomework = view.findViewById<Button>(R.id.btnAddHomeworks)
            val imgBack = view.findViewById<ImageView>(R.id.imgBack)

//            val imgRemoveLayout=view.findViewById<ImageView>(R.id.imgRemoveLayout)
            val imageAttched = view.findViewById<ImageView>(R.id.imageStudent)

            imageView = imageAttched

            imageAttched.setOnClickListener {
                toolBox.showConfirmationDialog(
                    context = requireContext(),
                    title = "Choose Upload Options",
                    message = "Please select gallery or camera",
                    positiveButtonText = "gallery",
                    positiveButtonAction = { openGallery() },
                    negativeButtonText = "camera",
                    negativeButtonAction = { openCamera() },
                    cancelable = true )
            }


            imgBack?.setOnClickListener {
                dialog.dismiss()
            }

            val schoolId = getSchoolId().trim()
            Log.d("AddHomework", "School ID: $schoolId")

            viewModelAllEmp.getAllEmployees(schoolId).observe(viewLifecycleOwner) { response ->
                if (response != null && response.employee != null && response.employee.isNotEmpty()) {
                    val employeeNames = response.employee.map { it.employee_name }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        employeeNames
                    )
                    spnSendBy.adapter = adapter
                } else {
                    Log.d("AddHomework", "No employees found")
                }
            }

            viewmodelAllClass.getClasses(schoolId).observe(viewLifecycleOwner) { response ->
                if (response != null && response.status) {
                    val classNames = response.data.map { it.class_name }
                    val classIds = response.data.map { it.class_id }

                    val classAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        classNames
                    )
                    etClassName.adapter = classAdapter

                    etClassName.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parentView: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedClassId = classIds[position]
                                Log.d("AddHomework", "Selected Class ID: $selectedClassId")

                                etClassId.text = selectedClassId

                                fetchSubjectsForClass(selectedClassId, schoolId, etSubjectName)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                            }
                        }
                } else {
                    Log.d("AddHomework", "No classes found")
                }
            }

            val calendar = Calendar.getInstance()
            val defaultDate = "${calendar.get(Calendar.DAY_OF_MONTH).padToTwoDigits()}/" +
                    "${(calendar.get(Calendar.MONTH) + 1).padToTwoDigits()}/" +
                    "${calendar.get(Calendar.YEAR)}"
            etDate.setText(defaultDate)

            etDate.setOnClickListener {
                showDatePickerDialog(etDate)
            }

            btnSubmitHomework.setOnClickListener {
                val homeworkDetail = etHomeworkDetail.text.toString().trim()
                val className = etClassName.selectedItem.toString().trim()
                val date = etDate.text.toString().trim()
                val teacher = spnSendBy.selectedItem?.toString()?.trim() ?: ""
                val subject = etSubjectName.selectedItem?.toString()?.trim() ?: ""
                val classId = etClassId.text.toString().trim() ?: ""
                val attachmentBase64 = ImageUtil.getBase64StringFromImageView(imageView) ?: "" // ImageUtil method
                // Log.d("AddMessage", "Attachment Base64: $attachmentBase64")

                if (homeworkDetail.isNotEmpty() && className.isNotEmpty() &&
                    date.isNotEmpty() && teacher.isNotEmpty() && subject.isNotEmpty()
                ) {
                    val homeworkData = mapOf(
                        "homework_detail" to homeworkDetail,
                        "classes" to classId,
                        "homework_date" to date,
                        // "set_by" to "$teacher (${SchoolId().getLoginRole(requireContext())})",
                        "set_by" to SchoolId().getLoginRole(requireContext()),
                        "school_id" to schoolId,
                        "subject" to subject,
                        "attachment" to attachmentBase64
                    )
                    // Log.d("Imge", "Imgage: $attachmentBase64")

//                    Toast.makeText(
//                        requireContext(),
//                        "Homework added Successfully",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    fetchdata()
                    observeHomeworkList()
                    initView()
                    viewModelAddHome.addHomework(homeworkData)

                    showHomeWorkDialog()
                    dialog.dismiss()
                    //showHomeWorkDialog()
                    fetchdata()
                    observeHomeworkList()
                    initView()
                    fetchdata()
                    adapter.notifyDataSetChanged()
                    fetchdata()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.setOnDismissListener { isDialogVisible = false }
            dialog.show()
        }
    }

    private fun fetchSubjectsForClass(className: String, schoolId: String, etSubjectName: Spinner) {
        val classwisesubject = mapOf(
            "class" to className,
            "school_id" to schoolId
        )

        viewModelCalssWise.getSubjectsByClass(classwisesubject)
            .observe(viewLifecycleOwner) { response ->
                if (response != null && response.status) {
                    val subjectList = response.data.map { it.subject_name }

                    val subjectAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        subjectList
                    )
                    /*  object : AdapterView.OnItemSelectedListener {
                          override fun onItemSelected(
                              parentView: AdapterView<*>,
                              view: View?,
                              position: Int,
                              id: Long
                          ) {*/
                    /* val selectedClassName =
                         parentView.getItemAtPosition(position) as String
 */
                    // Set the adapter to the subject spinner
                    etSubjectName.adapter = subjectAdapter
                } else {
                    Log.d("AddHomework", "No subjects found for selected class - Show dialog")
                    showSubkectDialog()  // Trigger the dialog
                    Log.d("AddHomework", "No subjects found for selected class")
                    val emptyList = emptyList<String>()
                    val emptyAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        emptyList
                    )
                    etSubjectName.adapter = emptyAdapter
                }
            }
    }

    private fun onEditClick(homework: Homework) {

        var data = mapOf<String, String>(
            "date" to homework.homework_date,
            "sentBy" to homework.set_by,
            "ClassId" to homework.classes,
            "subject" to homework.subject_name,
            "massage" to homework.homework_detail,
            "picture" to homework.attachment,
            "className" to homework.class_name,
            "subject" to homework.subject,
            "SrId" to homework.id,
        )
        toolBox.sendDataToFragment(requireContext(), updateHomework(), data)

        //dialogView1 = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_customhomeworkupdate, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView1)
            .setCancelable(true)

        val dialog = dialogBuilder.create()

        dialogView1?.let { view ->

            val etHomeworkDetail = dialogView1!!.findViewById<EditText>(R.id.edtHomeWorkDetail)
            val etClassName = dialogView1!!.findViewById<Spinner>(R.id.edtClass)
            val etClassId = dialogView1!!.findViewById<TextView>(R.id.edtId)
            val etSubjectName = dialogView1!!.findViewById<Spinner>(R.id.edtSubject)
            val etDate = dialogView1!!.findViewById<EditText>(R.id.edtDateHomeWork)
            val spnSendBy = dialogView1!!.findViewById<Spinner>(R.id.edtSendBy)
            val btnSubmitHomework = dialogView1!!.findViewById<Button>(R.id.btnAddHomeworks)
            val ID = dialogView1!!.findViewById<TextView>(R.id.IdHomework)
            val classID = dialogView1!!.findViewById<TextView>(R.id.classID)
            val imgBack = dialogView1!!.findViewById<ImageView>(R.id.imgBack)

            val imageAttched1 = dialogView1!!.findViewById<ImageView>(R.id.ImgEdit) // Correct initialization
            //val imageAttched2 = dialogView1!!.findViewById<ImageView>(R.id.ImgEditglid) // Correct initialization

            // Set up the image attachment click listener
            imageAttched1.setOnClickListener {
                toolBox.showConfirmationDialog(
                    context = requireContext(),
                    title = "Choose Upload Options",
                    message = "Please select gallery or camera",
                    positiveButtonText = "gallery",
                    positiveButtonAction = { openGallery() },
                    negativeButtonText = "camera",
                    negativeButtonAction = { openCamera() },
                    cancelable = true )
            }
            imgBack?.setOnClickListener {
                dialog.dismiss()
            }
            etHomeworkDetail.setText(homework.homework_detail)
            etDate.setText(homework.homework_date)
            classID.setText(homework.classes)
            ID.setText(homework.id)

            val imageUrl = ImageUtil.getFullImageUrl("homework", homework.attachment)

            Glide.with(imageAttched1.context)
                .load(imageUrl)
                .into(imageAttched1)

            /*
                        imageAttched1.setOnClickListener {
                            val context = imageAttched1.context
                            val intent = Intent(context, FullScreenImageActivity::class.java)
                            intent.putExtra("image_url", imageUrl)  // Pass the image URL to the FullScreen activity
                            context.startActivity(intent)
                        }
            */


            val schoolId = getSchoolId().trim() // Method to fetch school ID
            Log.d("AddHomework", "School ID: $schoolId")

            viewModelAllEmp.getAllEmployees(schoolId).observe(viewLifecycleOwner) { response ->
                if (response != null && response.employee.isNotEmpty()) {
                    val employeeNames = response.employee.map { it.employee_name }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        employeeNames
                    )
                    spnSendBy.adapter = adapter
                } else {
                    Log.d("AddHomework", "No employees found")
                }
            }
            viewmodelAllClass.getClasses(schoolId).observe(viewLifecycleOwner) { response ->
                if (response != null && response.status) {
                    val classNames = response.data.map { it.class_name }
                    val classIds = response.data.map { it.class_id }

                    val classAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        classNames
                    )
                    etClassName.adapter = classAdapter

                    etClassName.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parentView: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedClassId = classIds[position]
                                Log.d("AddHomework", "Selected Class ID: $selectedClassId")

                                // Update the Class ID TextView based on the selected class
                                etClassId.text = selectedClassId

                                // Fetch subjects for the selected class
                                fetchSubjectsForClass(selectedClassId, schoolId, etSubjectName)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // No action needed
                            }
                        }
                } else {
                    Log.d("AddHomework", "No classes found")
                }
            }

            etDate.setOnClickListener { showDatePickerDialog(etDate) }

            btnSubmitHomework.setOnClickListener {
                val homeworkDetail = etHomeworkDetail.text.toString().trim()
                val className = etClassName.selectedItem.toString().trim()
                val subject = etSubjectName.selectedItem?.toString()?.trim() ?: ""
                val date = etDate.text.toString().trim()
                val teacher = spnSendBy.selectedItem?.toString()?.trim() ?: ""
                val id = ID.text.toString().trim()
                val classid = classID.text.toString().trim()
                val attachmentBase64 = ImageUtil.getBase64StringFromImageView(imageAttched1)
                Log.d("AddMessage", "Attachment Base64: $attachmentBase64")
                if (attachmentBase64.isNullOrEmpty()) {
                    // Log.d("AddMessage", "No image attached or failed to convert image to Base64")
                } else {
                    // Log.d("AddMessage", "Image successfully converted to Base64: $attachmentBase64")
                }

                if (homeworkDetail.isNotEmpty() && className.isNotEmpty() &&
                    subject.isNotEmpty() && date.isNotEmpty() && teacher.isNotEmpty()
                ) {
                    val homeworkData = mapOf(
                        "homework_detail" to homeworkDetail,
                        "classes" to classid,
                        "homework_date" to date,
                        "set_by" to teacher,
                        "school_id" to SchoolId().getSchoolId(requireContext()),
                        "subject" to subject,
                        "id" to id,
                        "class_name" to className,
                        "attachment" to attachmentBase64)

                    Toast.makeText(requireContext(), SchoolId().getSchoolId(requireContext()), Toast.LENGTH_SHORT).show()

                    viewModelAddHome.updatedHomework(homeworkData as Map<String, String>)
                    showHomeWorkeditDialog()

                    Toast.makeText(
                        requireContext(),
                        "Homework Updated Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()

                    fetchdata()
                    observeHomeworkList()
                    initView()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            dialog.show()
        }
    }
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val defaultDate = "${day.padToTwoDigits()}/${(month + 1).padToTwoDigits()}/$year"
        editText.setText(defaultDate)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate =
                    "${selectedDay.padToTwoDigits()}/${(selectedMonth + 1).padToTwoDigits()}/$selectedYear"
                editText.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }
    private fun resetAttachment(textAttach: Button) {
        textAttach.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.global_button_background))
        textAttach.text = "Attach Image"
        textAttach.setTextColor(ContextCompat.getColor(requireContext(), R.color.global_cardview_background))
        photo1 = null
    }

//    private fun captureImage(IMAGE_CAPTURE_CODE: Int) {
//        checkPermissions()
//
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(intent, IMAGE_CAPTURE_CODE)
//        } else {
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
//        }
//    }
//
//    private fun openGallery(requestGallery: Int) {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
//        startActivityForResult(intent, requestGallery)
//    }

//    private fun createImageFile(): File {
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "IMG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        )
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                REQUEST_IMAGE_CAPTURE_1 -> {
//                    photo1 = data?.extras?.get("data") as Bitmap
//                    dialogView?.findViewById<ImageView>(R.id.imgeAttchedHomework1)?.setImageBitmap(photo1)
//                }
//                REQUEST_IMAGE_GALLERY_1 -> {
//                    val uri = data?.data
//                    photo1 = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
//                    dialogView?.findViewById<ImageView>(R.id.imgeAttchedHomework1)?.setImageBitmap(photo1)
//                }
//                REQUEST_IMAGE_CAPTURE_2 -> {
//                    photo2 = data?.extras?.get("data") as Bitmap
//                    dialogView1?.findViewById<ImageView>(R.id.ImgEdit)?.setImageBitmap(photo2)
//                }
//                REQUEST_IMAGE_GALLERY_2 -> {
//                    val uri = data?.data
//                    photo2 = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
//                    dialogView1?.findViewById<ImageView>(R.id.ImgEdit)?.setImageBitmap(photo2)
//                }
//            }
//        }
//    }



    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Depending on which request code was passed, capture or open gallery
                //captureImage(REQUEST_IMAGE_CAPTURE_1)
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //    private fun updateAttachmentButton(image: Bitmap, dialogView: View) {
//        val imageAttached = dialogView.findViewById<ImageView>(R.id.imgeAttchedHomework1)
//        imageAttached.setImageBitmap(image)
//        imageAttached.visibility = View.VISIBLE // Ensure the ImageView is visible
//        Log.d("UpdateImage", "Image updated in ImageView")
//    }
    private fun showHomeWorkeditDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Homework Updated Successfully",
            message = "Would You Like Add More Update?",
            positiveButtonText = "YES",
            positiveButtonAction = { toolBox.fragmentChanger(HomeWork(), requireContext()) },
            negativeButtonText = "NO",
            negativeButtonAction = {toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
    }
    private fun showHomeWorkDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Homework added Successfully",
            message = "Would you like to add more homework?",
            positiveButtonText = "YES",
            positiveButtonAction = { toolBox.fragmentChanger(HomeWork(), requireContext()) },
            negativeButtonText = "NO",
            negativeButtonAction = {toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
    }
    private fun showSubkectDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "NO Subject Found",
            message = "Would You Like Add Subject?",
            positiveButtonText = "Yes",
            positiveButtonAction = { toolBox.fragmentChanger(AssignSubject(), requireContext()) },
            negativeButtonText = "NO",
            negativeButtonAction = {toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
    }


    private fun Int.padToTwoDigits() = if (this < 10) "0$this" else "$this"


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a file to store the image
        val photoFile: File? = createImageFile()
        photoFile?.let {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.schoolerp.fileprovider",  // Use the correct authority
                it
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            if (cameraIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",  // suffix
            storageDir  // directory
        )
        currentPhotoPath = imageFile.absolutePath  // Save the file path to currentPhotoPath
        return imageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val photoFile = File(currentPhotoPath ?: return)
                    if (photoFile.exists()) {
                        // Decode the image file into a Bitmap
                        var bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                        // Correct the orientation of the image based on EXIF
                        val exif = ExifInterface(photoFile.absolutePath)
                        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                        // Rotate the bitmap based on EXIF orientation
                        bitmap = rotateImage(bitmap, orientation)

                        // Set the corrected image to the ImageView
                        imageView.setImageBitmap(bitmap)
                    }

                    // Clear the currentPhotoPath after using it
                    currentPhotoPath = null
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri: Uri = data?.data ?: return
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                        imageView.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun rotateImage(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()

        // Rotate based on EXIF data
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_NORMAL -> {} // No rotation needed
        }

        // Ensure the image is not stretched or distorted
        var rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // If the image is landscape, check if it is rotated 90 or 270 degrees, and adjust
        if (bitmap.width > bitmap.height) {
            // Apply a rotation to make sure the image displays properly (portrait mode)
            rotatedBitmap = rotateToPortrait(rotatedBitmap)
        }

        return rotatedBitmap
    }

    private fun rotateToPortrait(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        // If the image is landscape (width > height), rotate it to portrait
        if (bitmap.width > bitmap.height) {
            matrix.postRotate(90f) // Rotate to portrait
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}