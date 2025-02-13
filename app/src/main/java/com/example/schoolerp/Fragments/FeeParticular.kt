package com.example.schoolerp.Fragments.Fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.Activities.MainActivity
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.Fragments.CollectionFee
import com.example.schoolerp.Fragments.FeesCollectionDetailFragment
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.ViewModels.AddStudentViewModel
import com.example.schoolerp.ViewModels.AddStudentViewModelFactory
import com.example.schoolerp.databinding.FragmentFeeParticularBinding
import com.example.schoolerp.repository.AddFeeParticularRepository
import com.example.schoolerp.repository.AddStudentRepository
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.BaseOnInputeMessageRepository
import com.example.schoolerp.repository.GetFeeParticularRepository
import com.example.schoolerp.repository.TuitionFeesRepository
import com.example.schoolerp.repository.getStudentRepository
import com.example.schoolerp.viewmodel.AddFeeParticularViewModel
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.BaseOnInputMessageViewModel
import com.example.schoolerp.viewmodel.GetFeeParticularViewModel
import com.example.schoolerp.viewmodel.TuitionFeesViewModel
import com.example.schoolerp.viewmodel.getStudentViewModel
import com.example.schoolerp.viewmodelfactory.AddFeeParticularViewModelFactory
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.BaseOnInputeMessageViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetFeeParticularViewModelFactory
import com.example.schoolerp.viewmodelfactory.TuitionFeesViewModelFactory
import com.example.schoolerp.viewmodelfactory.getStudentViewModelFactory
import com.example.student.StudentInfo

class FeeParticular : Fragment() {
    private lateinit var binding: FragmentFeeParticularBinding
    private lateinit var viewModelInputeMessage: BaseOnInputMessageViewModel
    private lateinit var viewModelTuitionFees: TuitionFeesViewModel
    private lateinit var feeParticularViewModel: AddFeeParticularViewModel
    private lateinit var viewModelAllClass: AllClassViewModel
    private lateinit var viewModel: GetFeeParticularViewModel
    private var fineCount = 0
    private lateinit var viewModelgetStudent: getStudentViewModel
    private val dynamicFineLabels = mutableListOf<EditText>()
    private val dynamicFineAmounts = mutableListOf<EditText>()
    private lateinit var mProgress: ProgressDialog
    val toolBox = MethodLibrary()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeeParticularBinding.bind(
            inflater.inflate(
                R.layout.fragment_fee_particular,
                null
            )
        )
        setupViewgetfees()
        ViewModleTuitionFess()
        getSchoolId()
        ModelViewBaseonInpute()
        setupSpinners()
        //setupAllClassObserver()
        //  fetchData()
        // observeStudentData()
        ModelViewAddFeesTuition()
        setUpLisners()

        mProgress = createProgressDialog()

        return binding.root
    }
    private fun createProgressDialog(): ProgressDialog {
        return ProgressDialog(requireContext()).apply {
            setMessage("Please wait...")
            setCancelable(false)
        }
    }

    fun getSchoolId(): String {
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

    private fun ModelViewBaseonInpute() {
        val apiService = RetrofitHelper.getApiService()
        val repository = BaseOnInputeMessageRepository(apiService)
        val factory = BaseOnInputeMessageViewModelFactory(repository)
        viewModelInputeMessage =
            ViewModelProvider(this, factory).get(BaseOnInputMessageViewModel::class.java)
    }


    private fun setupSpinners() {
        val firstSpinner = binding.SelectionSpecific
        val secondSpinner = binding.SelectionSpecific
        val availableDataSpinnerLayout = binding.AvailableDataFeespParticularLayout
        val availableDataSpinner = binding.AvailableDataFeespParticular
        val onlyIdSpinner = binding.onlyId

        val spinner1Data = listOf(
            "all_class",
            "specific_class",
            "specific_student",
        ) // User-friendly display
        val spinner2Data = listOf(
            "all_class",
            "specific_class",
            "specific_student",
        ) // Internal values

        // Adapter for Spinner 1 (Display values for the user)
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinner1Data)
        firstSpinner.adapter = adapter1

        // Adapter for Spinner 2 (Display values for the user)
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinner1Data)
        secondSpinner.adapter = adapter2

        // Set default selections
        firstSpinner.setSelection(0)
        secondSpinner.setSelection(0)

        // Track spinner triggers to avoid infinite loops
        var isSpinner1Triggered = false
        var isSpinner2Triggered = false

        // Handle selection for the first spinner
        firstSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = spinner2Data[position] // Map to internal values

                // Check for specific class or student selection
                if (selectedItem == "specific_class" || selectedItem == "specific_student") {
                    availableDataSpinnerLayout.visibility = View.VISIBLE // Show the layout for specific class/student
                    handleSelection(selectedItem, availableDataSpinner, onlyIdSpinner)
                } else {
                    availableDataSpinnerLayout.visibility = View.GONE // Hide the layout if not specific
                }

                if (!isSpinner1Triggered) {
                    isSpinner2Triggered = true
                    secondSpinner.setSelection(position)
                    isSpinner2Triggered = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("SpinnerSetup", "Nothing selected in the first spinner")
            }
        }

        // Handle selection for the second spinner
        secondSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!isSpinner2Triggered) {
                    isSpinner1Triggered = true
                    firstSpinner.setSelection(position)
                    isSpinner1Triggered = false
                    val selectedItemInternal = spinner2Data[position]

                    if (selectedItemInternal == "specific_class" || selectedItemInternal == "specific_student") {
                        availableDataSpinnerLayout.visibility = View.VISIBLE // Show the layout for specific class/student
                        handleSelection(selectedItemInternal, availableDataSpinner, onlyIdSpinner)
                    } else {
                        availableDataSpinnerLayout.visibility = View.GONE // Hide the layout if not specific
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("SpinnerSetup", "Nothing selected in the second spinner")
            }
        }

        // Set touch listener for the second spinner
        availableDataSpinner.setOnTouchListener { _, _ ->
            val spinnerData = getSpinnerData(availableDataSpinner)
            showSearchableDialog(spinnerData, availableDataSpinner)
            true
        }
    }

    private fun synchronizeInternalSpinner(spinner1: Spinner, selectedItem: String) {
        val data = listOf(selectedItem) // Update internal spinner with the same data
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            data
        )
        spinner1.adapter = adapter

        // Automatically set the selected item in Spinner 1
        spinner1.setSelection(0)


    // Add touch listener to show a searchable dialog for the second spinner

    }

    private fun getSpinnerData(spinner: Spinner): List<String> {
        val adapter = spinner.adapter as ArrayAdapter<String>
        val dataList = mutableListOf<String>()
        for (i in 0 until adapter.count) {
            dataList.add(adapter.getItem(i) ?: "")
        }
        return dataList
    }

    private fun showSearchableDialog(data: List<String>, spinner: Spinner) {
        val dialog = AlertDialog.Builder(requireContext()) // Use Fragment's context
        val dialogView = layoutInflater.inflate(R.layout.dialog_searchable_spinner, null)
        dialog.setView(dialogView)

        val searchEditText = dialogView.findViewById<EditText>(R.id.searchEditText)
        val listView = dialogView.findViewById<ListView>(R.id.listView)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
        listView.adapter = adapter

        // Filter the list based on user input
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val alertDialog = dialog.create()
        alertDialog.show()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedValue = adapter.getItem(position) ?: return@setOnItemClickListener
            alertDialog.dismiss() // Dismiss the dialog only once
            val spinnerAdapter = spinner.adapter as ArrayAdapter<String>
            val index = spinnerAdapter.getPosition(selectedValue)
            alertDialog.dismiss() // Dismiss the dialog only once
            spinner.setSelection(index)
            alertDialog.dismiss() // Dismiss the dialog only once
            TuitionFees()

        }
    }

    private fun handleSelection(
        selectedItem: String,
        availableDataSpinner: Spinner,
        onlyIdSpinner: Spinner
    ) {
        fetchDataBasedOnSelection(selectedItem) { data ->
            Log.d("SpinnerSetup", "Fetched data for $selectedItem: $data")

            val availableData = mutableListOf<String>()
            val onlyIdData = mutableListOf<String>()

            if (data.isEmpty()) {
                availableData.add("No data available")
                onlyIdData.add("No data available")
            } else {
                data.forEach { entry ->
                    // Assume format "Name (ID)"
                    val regex = """(.*?)\s?\((\d+)\)""".toRegex()
                    val matchResult = regex.find(entry)
                    if (matchResult != null) {
                        val name = matchResult.groupValues[1].trim() // Remove leading/trailing spaces
                        val id = matchResult.groupValues[2] // ID from within parentheses
                        availableData.add(name)
                        onlyIdData.add(id)
                    }
                }
            }


            // Update the spinners on the main thread
            requireActivity().runOnUiThread {
                // Update the AvailableData spinner
                val availableAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    availableData
                )
                availableDataSpinner.adapter = availableAdapter
                TuitionFees()

                // Update the OnlyId spinner
                val onlyIdAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    onlyIdData
                )
                onlyIdSpinner.adapter = onlyIdAdapter

                // Set listener to synchronize spinners
                availableDataSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        // Set the second spinner's selection based on the position of the first spinner
                        onlyIdSpinner.setSelection(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}

                }
            }
        }
    }

    private fun fetchDataBasedOnSelection(selectedItem: String, callback: (List<String>) -> Unit) {
        val schoolId = SchoolId().getSchoolId(requireContext())
        Log.d("SpinnerSetup", "School ID: $schoolId")

        val inputMap = mapOf(
            "classes" to selectedItem,
            "students" to selectedItem,
            "school_id" to schoolId
        )

        Log.d("SpinnerSetup", "Input Map: $inputMap")
        viewModelInputeMessage.getBaseOnInputMessage(inputMap)

        // Observe the API response
        viewModelInputeMessage.baseOnInputMessageResponse.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Log.d("SpinnerSetup", "API response received: ${response.data}")

                if (response.status) {
                    Log.d("SpinnerSetup", "API response status is true, data: ${response.data}")

                    // Process the data based on the selected item
                    val dataToDisplay = when (selectedItem) {
                        "specific_class" -> {
                            val classesData = handleClassesData(response.data.classes)
                            Log.d("SpinnerSetup", "Classes Data: $classesData")
                            classesData
                        }

                        "specific_student" -> {
                            val studentsData = handleStudentsData(response.data.students)
                            Log.d("SpinnerSetup", "Students Data: $studentsData")
                            studentsData
                        }

                        else -> {
                            Log.d("SpinnerSetup", "No matching selection for: $selectedItem")
                            emptyList()
                        }
                    }
                    callback(dataToDisplay)  // Callback with the processed data
                } else {
                    Log.e("SpinnerSetup", "API response status is false: ${response.Message}")
                    callback(emptyList())  // Callback with an empty list if the response is not successful
                }
            }
            result.onFailure { error ->
                Log.e("SpinnerSetup", "API call failed: ${error.message}")
                callback(emptyList())  // Callback with an empty list in case of failure
            }
        }
    }

    private fun handleClassesData(classesData: Any?): List<String> {
        return if (classesData is List<*>) {
            // Process the class data and return a formatted string
            classesData.mapNotNull { classItem ->
                (classItem as? Map<*, *>)?.let {
                    val classId = it["class_id"] as? String
                    val className = it["class_name"] as? String
                    if (classId != null && className != null) {
                        "$className ($classId)" // Format: class_name (class_id)
                    } else {
                        null
                    }
                }
            }
        } else {
            emptyList()
        }
    }

    private fun handleStudentsData(studentsData: Any?): List<String> {
        return if (studentsData is List<*>) {
            studentsData.mapNotNull { student ->
                (student as? Map<*, *>)?.let {
                    val studentId = it["student_id"] as? String
                    val studentName = it["student_name"] as? String
                    if (studentId != null && studentName != null) {
                        val formattedName = studentName.trim().replace(Regex("\\s+"), " ")
                        "$formattedName ($studentId)"
                    } else {
                        null
                    }
                }
            }
        } else {
            emptyList()
        }
    }

    private fun handleEmployeesData(employeesData: Any?): List<String> {
        return when (employeesData) {
            is List<*> -> {
                // Check if it's a list of Maps
                if (employeesData.isNotEmpty() && employeesData[0] is Map<*, *>) {
                    // Map the list of maps to employee names
                    employeesData.mapNotNull { (it as? Map<*, *>)?.get("employee_name") as? String }
                } else {
                    Log.d("SpinnerSetup", "Employees data is empty or malformed: $employeesData")
                    emptyList()
                }
            }

            else -> {
                Log.d("SpinnerSetup", "Employees data is not a List: $employeesData")
                emptyList()
            }
        }
    }

    private fun ViewModleTuitionFess() {
        val apiService = RetrofitHelper.getApiService()
        val repository = TuitionFeesRepository(apiService)
        val factory = TuitionFeesViewModelFactory(repository)
        viewModelTuitionFees =
            ViewModelProvider(this, factory).get(TuitionFeesViewModel::class.java)
    }

    private fun TuitionFees() {
        val schoolId = getSchoolId()

        val className = binding.AvailableDataFeespParticular.selectedItem?.toString() ?: ""
        val studentName = binding.AvailableDataFeespParticular.selectedItem?.toString() ?: ""

        Log.d("FeeParticular", "Selected class name: $className")
        Log.d("FeeParticular", "Selected student name: $studentName")
        Log.d("FeeParticular", "School ID: $schoolId")

        val params = mapOf(
            "class_name" to className,
            "st_name" to studentName,
            "school_id" to schoolId.toString()
        )

        Log.d("FeeParticular", "Fetching tuition fees with parameters: $params")

        viewModelTuitionFees.fetchTuitionData(params)

        viewModelTuitionFees.tuitionData.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == "success") {
                val classFee = response.data.class_name_tuition_fees
                val studentFee = response.data.student_class_tuition_fees

                // Extract and display numeric data only
                val numericClassFee = classFee.toIntOrNull()?.toString() ?: ""
                val numericStudentFee = studentFee.toIntOrNull()?.toString() ?: ""

                val displayNumber = when {
                    numericStudentFee.isNotEmpty() -> numericStudentFee
                    numericClassFee.isNotEmpty() -> numericClassFee
                    else -> "00"
                }

                binding.tuitionFeesTextView.text = displayNumber
                Log.d("FeeParticular", "Tuition fees fetched and displayed: $displayNumber")
            } else {
                val errorMessage = response?.data?.student_class_tuition_fees ?: ""
                val number =
                    errorMessage.split(" ").firstOrNull { it.toIntOrNull() != null } ?: "00"

                // Show only the number in the center
                binding.tuitionFeesTextView.text = number
                Log.e("FeeParticular", "Error fetching tuition fees")
                Log.e("FeeParticular", "Error message: $errorMessage")
            }
        }
    }

    private fun ModelViewAddFeesTuition() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AddFeeParticularRepository(apiService)
        val factory = AddFeeParticularViewModelFactory(repository)
        feeParticularViewModel =
            ViewModelProvider(this, factory).get(AddFeeParticularViewModel::class.java)

    }

    private fun setUpLisners() {
        binding.btnAddPartculaarFees.setOnClickListener {
            AddFees()
        }
        // Initially hide the fines container
        binding.finesContainer.visibility = View.GONE

        // Handle "Add More" button click
        binding.addmore.setOnClickListener {
            if (binding.finesContainer.visibility == View.GONE) {
                binding.finesContainer.visibility = View.VISIBLE
            }
            addFineRow()
        }
    }

    private fun AddFees() {
        mProgress.show()
        val schoolId = getSchoolId()
        val className = binding.onlyId.selectedItem.toString()
        val specific = binding.SelectionSpecific.selectedItem.toString()


        // Collect static inputs
        val particularLabels = listOf(
            binding.AdmissionFees.text.toString(),
            binding.RegistrationFees.text.toString(),
            binding.ArtMaterial.text.toString(),
            binding.Transport.text.toString(),
            binding.Books.text.toString(),
            binding.Uniform.text.toString(),
            binding.Fine.text.toString(),
            binding.TermlyTuitionFees.text.toString()
        ).filter { it.isNotBlank() } // Exclude empty inputs

        val prefixAmounts = listOf(
            binding.AdmissionFeesPrefixamount.text.toString(),
            binding.AmountRegistrationFees.text.toString(),
            binding.AmountArtMaterial.text.toString(),
            binding.AmountTransport.text.toString(),
            binding.AmountBooks.text.toString(),
            binding.AmountUniform.text.toString(),
            binding.AmountFine.text.toString(),
            binding.tuitionFeesTextView.text.toString()
        ).filter { it.isNotBlank() } // Exclude empty inputs

        // Collect dynamic inputs
        val dynamicLabels = dynamicFineLabels.map { it.text.toString() }.filter { it.isNotBlank() }
        val dynamicAmounts =
            dynamicFineAmounts.map { it.text.toString() }.filter { it.isNotBlank() }

        // Combine static and dynamic inputs
        val allLabels = particularLabels + dynamicLabels
        val allAmounts = prefixAmounts + dynamicAmounts

        Log.d("FeeParticular", "Particular Labels: $allLabels")
        Log.d("FeeParticular", "Prefix Amounts: $allAmounts")

        if (allLabels.size == allAmounts.size)
        {
            val formattedLabels = allLabels.joinToString(", ") { "\"$it\"" }
            val formattedAmounts = allAmounts.joinToString(", ") { "\"$it\"" }

            // for specific class
            if (binding.SelectionSpecific.selectedItem.equals("specific_class")){
                val params = mapOf(
                    "classes" to specific,
                    "search_specific" to className,
                    "particular_label" to "[${formattedLabels}]",
                    "prefix_amount" to "[${formattedAmounts}]",
                    //"student_id" to "47",
                    "school_id" to schoolId,
                )

                val paramsLog = params.entries.joinToString(", ") { "${it.key}=${it.value}" }
                Log.d("FeeParticular", "Parameters to API: {$paramsLog}")

                // Trigger the ViewModel function to call the API
                feeParticularViewModel.addFeeParticular(params)

                // Observe the result from the ViewModel
                feeParticularViewModel.addFeeParticularResponse.observe(viewLifecycleOwner) { response ->
                    if (response != null && response.status) {
                        Log.d("FeeParticular", "Data inserted: ${response.message}")
                        Toast.makeText(context, "Fees added successfully!", Toast.LENGTH_SHORT).show()
                        mProgress.dismiss()
                        showAddFeeParticularDialog()
                    } else {
                        Log.e("FeeParticular", "Error: ${response?.message}")
                        Toast.makeText(context, "Failed to add fees.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // for specific student
            if (binding.SelectionSpecific.selectedItem.equals("specific_student")){
                //Toast.makeText(requireContext(), "hello", Toast.LENGTH_SHORT).show()
                val params = mapOf(
                    "classes" to specific,
                    //"search_specific" to className,
                    "particular_label" to "[${formattedLabels}]",
                    "prefix_amount" to "[${formattedAmounts}]",
                     "student_id" to "84",
                    "school_id" to schoolId,
                )

                val paramsLog = params.entries.joinToString(", ") { "${it.key}=${it.value}" }
                Log.d("FeeParticular", "Parameters to API: {$paramsLog}")

                // Trigger the ViewModel function to call the API
                feeParticularViewModel.addFeeParticular(params)

                // Observe the result from the ViewModel
                feeParticularViewModel.addFeeParticularResponse.observe(viewLifecycleOwner) { response ->
                    if (response != null && response.status) {
                        Log.d("FeeParticular", "Data inserted: ${response.message}")
                        Toast.makeText(context, "Fees added successfully!", Toast.LENGTH_SHORT).show()
                        mProgress.dismiss()
                        showAddFeeParticularDialog()
                    } else {
                        Log.e("FeeParticular", "Error: ${response?.message}")
                        Toast.makeText(context, "Failed to add fees.", Toast.LENGTH_SHORT).show()
                    }
                }
            }


            else {
                Log.e("FeeParticular", "Mismatch between labels and amounts.")
                Toast.makeText(context, "Mismatch between labels and amounts.", Toast.LENGTH_SHORT).show()
            }

        }

        }

    private fun addFineRow() {
        fineCount++ // Increment the fine count for unique identifiers

        // Create a new horizontal LinearLayout programmatically
        val fineRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(15, 15, 15, 15)
            }
        }

        // Create the "Fine" EditText
        val fineEditText = EditText(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                0,
                45.dpToPx(requireContext()),
                1f
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            background = ContextCompat.getDrawable(context, R.drawable.edit_text_background2)
            gravity = android.view.Gravity.CENTER
            hint = "Write"
        }

        // Create the "Amount Fine" EditText
        val amountEditText = EditText(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                0,
                45.dpToPx(requireContext()),
                0.5f
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            background = ContextCompat.getDrawable(context, R.drawable.edit_text_background2)
            gravity = android.view.Gravity.CENTER
            hint = "00"
        }

        // Create the "Remove" button
        val removeButton = ImageButton(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setImageResource(R.drawable.minus) // Add your minus icon drawable
            background = ContextCompat.getDrawable(context, R.drawable.background_absent)
            setOnClickListener {
                removeFineRow(fineRow)
            }
        }

        fineRow.addView(fineEditText)
        fineRow.addView(amountEditText)
        fineRow.addView(removeButton)

        dynamicFineLabels.add(fineEditText)
        dynamicFineAmounts.add(amountEditText)

        binding.finesContainer.addView(fineRow)
    }

    private fun removeFineRow(fineRow: LinearLayout) {
        // Remove the row from the container
        binding.finesContainer.removeView(fineRow)

        // Find and remove the associated EditTexts from tracking lists
        fineRow.children.forEach { view ->
            if (view is EditText) {
                dynamicFineLabels.remove(view)
                dynamicFineAmounts.remove(view)
            }
        }
    }

    private fun Int.dpToPx(context: android.content.Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }


    private fun fetchData() {
        val apiService = RetrofitHelper.getApiService()
        val repository = getStudentRepository(apiService)
        val factory = getStudentViewModelFactory(repository)
        viewModelgetStudent = ViewModelProvider(this, factory).get(getStudentViewModel::class.java)

        // val schoolId = getSchoolId().trim() // Ensure schoolId is trimmed
        viewModelgetStudent.fetchStudentsBySchoolId(SchoolId().getSchoolId(requireContext()))
    }

    private fun observeStudentData() {
        // Observe the list of students
        viewModelgetStudent.students.observe(viewLifecycleOwner) { students ->
            if (students != null && students.isNotEmpty()) {
                setupStudentSpinners(students)
            } else {
                Toast.makeText(requireContext(), "No students found", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe error messages
        viewModelgetStudent.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupStudentSpinners(classList: List<Student>) {
        val studentNamesWithIds = mutableListOf("Select Student (Name - ID)")
        val studentIds = mutableListOf("Select Student ID")

        // Populate spinner items
        classList.forEach { student ->
            studentNamesWithIds.add("${student.st_name} - ${student.id}")
            studentIds.add(student.id)
        }

        // Set up the first spinner
        val studentNamesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            studentNamesWithIds
        )
        studentNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.AvailableDataFeespParticular.adapter = studentNamesAdapter

        // Handle spinner selection
        binding.AvailableDataFeespParticular.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                  /*  if (position > 0) {
                        val selectedStudent = classList[position - 1]
                        Log.d(
                            "FeeParticular",
                            "Selected student: ${selectedStudent.st_name} - ${selectedStudent.id}"
                        )
                    } else {
                        Log.d("FeeParticular", "No student selected")
                    }
                }*/
                    val selectedClassName =
                        parentView.getItemAtPosition(position) as String

                    // Filter class IDs based on the selected class name
                    val filteredClassIds =
                        classList.filter { it.st_name == selectedClassName }
                            .map { it.id.toString() }

                    // Ensure filteredClassIds is not empty before updating the second spinner
                    if (filteredClassIds.isNotEmpty()) {
                        // Update the second spinner with the filtered class IDs
                        val updatedAdapter2 = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            filteredClassIds
                        )
                        updatedAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.onlyId.adapter = updatedAdapter2
                    } else {
                        // If no matching class IDs, set the default "Select Class ID"
                        binding.onlyId.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            listOf("Select Class ID")
                        )
                    }
                }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d("FeeParticular", "Nothing selected in spinner")
                }
            }
    }
    private fun resetSecondSpinner() {
        binding.onlyId.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Select Student ID")
        )
    }
    private fun setupAllClassObserver() {
        val factory = AllClassViewModelFactory(AllClassRepository())
        viewModelAllClass = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)
        // Observe the LiveData from ViewModel
        viewModelAllClass.getClasses(SchoolId().getSchoolId(requireContext()))
            .observe(viewLifecycleOwner) { response ->
                response?.data?.let { classList ->
                    if (classList.isNotEmpty()) {
                        // Extract the class names and class IDs from the classList
                        val classNames =
                            mutableListOf("Select Class") // Add "Select Class" as the default item
                        val classIds =
                            mutableListOf("Select Class ID") // Add "Select Class ID" as the default item

                        // Add class names and class IDs from the class list
                        classNames.addAll(classList.map { it.class_name })
                        classIds.addAll(classList.map { it.class_id.toString() })

                        // Create adapter for the first spinner with class names (including "Select Class")
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            classNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        // Create adapter for the second spinner with class IDs (including "Select Class ID")
                        val adapter2 = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            classIds
                        )
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        // Set the adapters to the respective Spinners
                        binding.AvailableDataFeespParticular.adapter = adapter
                        binding.onlyId.adapter = adapter2

                        // Set the OnItemSelectedListener for the first Spinner
                        binding.AvailableDataFeespParticular.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parentView: AdapterView<*>,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    // Get the selected class name (from first spinner)
                                    val selectedClassName =
                                        parentView.getItemAtPosition(position) as String

                                    // Filter class IDs based on the selected class name
                                    val filteredClassIds =
                                        classList.filter { it.class_name == selectedClassName }
                                            .map { it.class_id.toString() }

                                    // Ensure filteredClassIds is not empty before updating the second spinner
                                    if (filteredClassIds.isNotEmpty()) {
                                        // Update the second spinner with the filtered class IDs
                                        val updatedAdapter2 = ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_spinner_item,
                                            filteredClassIds
                                        )
                                        updatedAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        binding.onlyId.adapter = updatedAdapter2
                                    } else {
                                        // If no matching class IDs, set the default "Select Class ID"
                                        binding.onlyId.adapter = ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_spinner_item,
                                            listOf("Select Class ID")
                                        )
                                    }
                                }

                                override fun onNothingSelected(parentView: AdapterView<*>) {
                                    // Handle case when nothing is selected (optional)
                                    // Set the default "Select Class ID" in the second spinner
                                    binding.onlyId.adapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        listOf("Select Class ID")
                                    )
                                }
                            }
                    } else {
                        // If no classes are found, show a Toast
                        Toast.makeText(requireContext(), "No classes found", Toast.LENGTH_SHORT)
                            .show()
                    }
                } ?: run {
                    // Handle case when response or data is null
                    Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT)
                        .show()
                }

            }
    }
     private fun showAddFeeParticularDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(), // or requireContext() if in a Fragment
            title = "Fees Added Successfully",
            message = "Would you want to add more Fee data?",
            positiveButtonText = "YES",
            positiveButtonAction = { toolBox.fragmentChanger(FeeParticular(), requireContext()) },
            negativeButtonText = "NO",
            negativeButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
            cancelable = false
        )
    }

    private fun setupViewgetfees() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetFeeParticularRepository(apiService)
        val factory = GetFeeParticularViewModelFactory(repository)
        viewModel =
            ViewModelProvider(this, factory).get(GetFeeParticularViewModel::class.java)
    }

    private fun fetchAttendanceData(studentID: String) {
        val schoolId = SchoolId().getSchoolId(requireContext())
        val StudentId = studentID

        if (!schoolId.isNullOrEmpty() && StudentId != null) {
            // Fetch fee particulars from the API
            viewModel.fetchFeeParticulars(schoolId, StudentId)
                .observe(viewLifecycleOwner) { response ->
                    if (response != null && response.status) {
                        // Pass the fee data to the fragment
                        response.data?.let { feeData ->
                            feeData.forEach { fee ->
                                val prefixAmountList = fee.prefix_amount
                                val particularLabel = fee.particular_label


                                toolBox.showSetText(particularLabel[0], binding.TermlyTuitionFees)
                                toolBox.showSetText(particularLabel[1], binding.AdmissionFees)
                                toolBox.showSetText(particularLabel[2], binding.RegistrationFees)
                                toolBox.showSetText(particularLabel[3], binding.ArtMaterial)
                                toolBox.showSetText(particularLabel[4], binding.Transport)
                                toolBox.showSetText(particularLabel[5], binding.Books)
                                toolBox.showSetText(particularLabel[6], binding.Uniform)
                                toolBox.showSetText(particularLabel[7], binding.Fine)

                                toolBox.showSetText(prefixAmountList[0], binding.tuitionFeesTextView)
                                toolBox.showSetText(prefixAmountList[1], binding.AdmissionFeesPrefixamount)
                                toolBox.showSetText(prefixAmountList[2], binding.AmountRegistrationFees)
                                toolBox.showSetText(prefixAmountList[3], binding.AmountArtMaterial)
                                toolBox.showSetText(prefixAmountList[4], binding.AmountTransport)
                                toolBox.showSetText(prefixAmountList[5], binding.AmountBooks)
                                toolBox.showSetText(prefixAmountList[6], binding.AmountUniform)
                                toolBox.showSetText(prefixAmountList[7], binding.AmountFine)

                                // Toast.makeText(requireContext(), prefixAmountList[0], Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
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
}
