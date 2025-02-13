package com.example.schoolerp.Fragments

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.helpers.ClearErrorTextWatcher
import com.example.helpers.MethodLibrary
import com.example.helpers.validation
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentPaySalaryBinding
import com.example.schoolerp.repository.AllEmployeesRepository
import com.example.schoolerp.repository.PaySalaryRepository
import com.example.schoolerp.util.LoadingUtil
import com.example.schoolerp.viewmodel.AllEmployeesViewModel
import com.example.schoolerp.viewmodel.PaySalaryViewModel
import com.example.schoolerp.viewmodelfactory.AllEmployeesViewModelFactory
import com.example.schoolerp.viewmodelfactory.PaySalaryViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class PaySalary : Fragment() {
    private lateinit var binding: FragmentPaySalaryBinding
    private lateinit var etSalaryDate: EditText
    private lateinit var viewModel: PaySalaryViewModel
    private lateinit var viewModelEmp: AllEmployeesViewModel
    lateinit var loader: ImageView
    private lateinit var mProgress: ProgressDialog
    val toolBox = MethodLibrary()
    private lateinit var connectivityReceiver: BroadcastReceiver
    private var isDataObserved = false

    val toolbox = validation()
    private var selectedMonthFromSpinner: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentPaySalaryBinding.bind(inflater.inflate(R.layout.fragment_pay_salary, null))

        setupListeners()
        setupViewModel()
        observeViewModel()
        setupclear()
        setupViewEmp()
        cleardata()
        setSpinner()
        setupTextChangeListeners()
        loader = binding.loaderImageView
        mProgress = createProgressDialog()
        registerConnectivityReceiver()
        observeData()
        return binding.root
    }

    private fun registerConnectivityReceiver() {
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let {
                    if (isInternetAvailable(it)) {
                        observeData()
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

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(connectivityReceiver)
    }

    private fun createProgressDialog(): ProgressDialog {
        return ProgressDialog(requireContext()).apply {
            setMessage("Please wait...")
            setCancelable(false)
        }
    }

    private fun startLoaderAnimation() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.loader)
            .into(loader)
    }

    private fun showLoader() {
        loader.visibility = View.VISIBLE
        startLoaderAnimation()

    }

    private fun hideLoader() {
        loader.visibility = View.GONE

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

        return schoolId ?: "defaultSchoolId" // Return the schoolId or a default value
    }

    private fun setupclear() {


//        binding.etSalaryAmount.addTextChangedListener(
//            ClearErrorTextWatcher(
//                binding.empSalaryAmountLayout,
//                "Enter Salary Amount"
//            )
//        )
//        binding.etAnyBonus.addTextChangedListener(
//            ClearErrorTextWatcher(
//                binding.empBonusLayout,
//                "Enter Bonus"
//            )
//        )
//        binding.etAnyDeduction.addTextChangedListener(
//            ClearErrorTextWatcher(
//                binding.empDeductionLayout,
//                "Enter Deduction"
//            )
//        )
        binding.etNetPaid.addTextChangedListener(
            ClearErrorTextWatcher(
                binding.empPaidLayout,
                "Enter Net Paid"
            )
        )
    }

    private fun setupListeners() {
        /* binding.etEmployeesDateOfPay.setOnClickListener {
            showDatePickerDialog()
        }*/

        binding.btnSubmitSalary.setOnClickListener {
            submitSalary()
        }
    }

    private fun setupViewModel() {
        val apiService = RetrofitHelper.getApiService()
        val repository = PaySalaryRepository(apiService)
        val factory = PaySalaryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PaySalaryViewModel::class.java)
    }

    private fun submitSalary() {
        if (validateEmployeeData()) {

            //showLoader()
            mProgress.show()
            val paySalaryData = mapOf(
                // "employee_id" to binding.etEmployeeId.text.toString().trim(),
                "employee_name" to binding.etEmployeesName.selectedItem.toString().trim(),
                "employee_role" to binding.etEmployeesRole.selectedItem.toString().trim(),
                "salary_month" to binding.spinnerSalaryMonth.selectedItem.toString().trim(),
                "salary_amount" to binding.etSalaryAmount.text.toString().trim(),
                "date_of_pay" to binding.etEmployeesDateOfPay.text.toString().trim(),
                "bouns" to binding.etAnyBonus.text.toString().trim(),
                "deduction" to binding.etAnyDeduction.text.toString().trim(),
                "net_paid" to binding.etNetPaid.text.toString().trim(),
                "school_id" to SchoolId().getSchoolId(requireContext())

            )


            viewModel.paySalary(paySalaryData)
        }
    }

    private fun observeViewModel() {
        val loaderImageView = binding.loaderImageView

        viewModel.paySalaryResponse.observe(viewLifecycleOwner) { response ->


            Log.d("PaySalary", "Response Message: ${response.message}")

            val messageToShow =
                response.message ?: "Income data added successfully" // Default message if null
            mProgress.dismiss()

            showAddFeeParticularDialog()
            if (response.status) {


//                Toast.makeText(context, messageToShow, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to add salary: $messageToShow", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun cleardata() {
        binding.etEmployeesName.setSelection(0)
        binding.etEmployeesRole.setSelection(0)
        binding.spinnerSalaryMonth.setSelection(0)
        binding.etSalaryAmount.text?.clear()
        binding.etEmployeesDateOfPay.text?.clear()
        binding.etAnyBonus.text?.clear()
        binding.etAnyDeduction.text?.clear()
        binding.etNetPaid.text?.clear()
    }

    /*private fun clearForm() {
        // Optionally clear the input fields
        binding.etEmployeesName.selectedItem.clear()
        binding.etEmployeesRole.text.clear()
        binding.etFHName.text.clear()
        binding.etSalaryMonth.text.clear()
        binding.etSalaryAmount.text.clear()
        binding.etEmployeesDateOfPay.text.clear()
        binding.etAnyBonus.text.clear()
        binding.etAnyDeduction.text.clear()
        binding.etNetPaid.text.clear()
    }*/

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val selectedMonthFromSpinner = binding.spinnerSalaryMonth.selectedItemPosition

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Update the Spinner's position based on the selected month
                binding.spinnerSalaryMonth.setSelection(selectedMonth)

                // Format the date as "yyyy-MM-dd"
                val formattedDate =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.etEmployeesDateOfPay.setText(formattedDate)
            }, year, selectedMonthFromSpinner, day)

        datePickerDialog.show()
    }

    private fun setSpinner() {
        val months = resources.getStringArray(R.array.select_month)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSalaryMonth.adapter = adapter

        binding.spinnerSalaryMonth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedMonthFromSpinner =
                        position  // Update the selected month based on user selection
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Optional: Handle when nothing is selected
                }
            }

        binding.etEmployeesDateOfPay.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun validateEmployeeData(): Boolean {
        mProgress.dismiss()

        val name = binding.etEmployeesName.selectedItemPosition.toString().trim()
        val role = binding.etEmployeesRole.selectedItemPosition.toString().trim()
        val etemployeesdatepay = binding.etEmployeesDateOfPay.text.toString().trim()
        val edtsalaryMonth = binding.spinnerSalaryMonth.selectedItem.toString().trim()
        val edtsalaryamount = binding.etSalaryAmount.text.toString().trim()
//        val edtAnyBonus = binding.etAnyBonus.text.toString().trim()
        val edtNetpaid = binding.etNetPaid.text.toString().trim()
//        val edtanydection = binding.etAnyDeduction.text.toString().trim()

        return when {
            name.isEmpty() || name == "0" -> {
                showToast("Select a valid Employee Name")
                false
            }

            role.isEmpty() || role == "0" -> {
                showToast("Select a valid Employee Role")
                false
            }

            etemployeesdatepay.isEmpty() -> {
                showError(binding.etEmployeesDateOfPay, "Enter Date of Payment")
                false
            }

            edtsalaryMonth.isEmpty() || edtsalaryMonth == "--Select Month--" -> {
                showToast("Select a valid Month")
                false
            }

            edtsalaryamount.isEmpty() -> {
                showError(binding.etSalaryAmount, "Enter Salary Amount")
                false
            }
//            edtAnyBonus.isEmpty() -> {
//                showError(binding.etAnyBonus, "This field is mandatory")
//                false
//            }
            edtNetpaid.isEmpty() -> {
                showError(binding.etNetPaid, "This field is mandatory")
                false
            }
//            edtanydection.isEmpty() -> {
//                showError(binding.etAnyDeduction, "This field is mandatory")
//                false
//            }
            else -> true
        }
    }


    private fun showError(view: EditText, message: String) {
        view.error = message
        view.requestFocus()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showAddFeeParticularDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Salary Paid Successfully.",
            message = "Would you like to pay more salary?",
            positiveButtonText = "Yes",
            positiveButtonAction = { toolBox.fragmentChanger(PaySalary(), requireContext()) },
            negativeButtonText = "No",
            negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
    }


// Call this function where necessary, for example after fetching data or when inputs change

    /*fun valid() {
        if (toolbox.validation(
                //   binding.etEmployeeId,
                // binding.etEmployeesName,
                //  binding.etEmployeesRole,
                binding.etEmployeesDateOfPay,
                binding.etSalaryMonth,
                binding.etSalaryAmount,
                binding.etAnyBonus,
                binding.etNetPaid,
                binding.etAnyDeduction,)
        ) {
            submitSalary()
        } else {
            //  toolbox.errorLayout(binding.empIdLayout, "Enter Employee Id")
            //  toolbox.errorLayout(binding.empNameLayout, "Enter Employee Name")
            // toolbox.errorLayout(binding.empRoleLayout, "Enter Employee Role/Position")
            toolbox.errorLayout(binding.empSalaryDateLayout, "Select/Enter a Date")
            toolbox.errorLayout(binding.empSalaryMonthLayout, "Enter Salary Month")
            toolbox.errorLayout(binding.empSalaryAmountLayout, "Enter Salary Amount")
            toolbox.errorLayout(binding.empBonusLayout, "This Field is Mandatory")
            toolbox.errorLayout(binding.empPaidLayout, "This Field is Mandatory")
            toolbox.errorLayout(binding.empDeductionLayout, "This Field is Mandatory")
        }
    }*/
    private fun setupViewEmp() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AllEmployeesRepository(apiService)
        val factory = AllEmployeesViewModelFactory(repository)
        viewModelEmp = ViewModelProvider(this, factory).get(AllEmployeesViewModel::class.java)
    }

    private fun observeData() {
        if (isDataObserved) return

        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
            return
        }

        isDataObserved = true // Mark as observed

        val schoolId = getSchoolId().trim()
        viewModelEmp.getAllEmployees(schoolId).observe(viewLifecycleOwner) { response ->
            if (response != null && !response.employee.isNullOrEmpty()) {
                val employeeNames = mutableListOf("Please select Employee Name")
                val employeeRoles = mutableListOf("Please select Role")
                val employeeMap = mutableMapOf<String, String>()
                val employeeSalaries = mutableMapOf<String, String>()

                for (employee in response.employee) {
                    val employeeName = employee.employee_name ?: "Unknown Name"
                    val employeeRole = employee.employee_role ?: "Unknown Role"
                    val monthlySalary = employee.monthly_salary ?: "N/A"

                    employeeNames.add(employeeName)
                    employeeRoles.add(employeeRole)
                    employeeMap[employeeName] = employeeRole
                    employeeSalaries[employeeName] = monthlySalary
                }

                val nameAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    employeeNames
                )
                nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.etEmployeesName.adapter = nameAdapter

                val roleAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    employeeRoles.distinct()
                )
                roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.etEmployeesRole.adapter = roleAdapter
                binding.etEmployeesRole.visibility = View.GONE

                binding.etEmployeesName.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parentView: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedName = parentView.getItemAtPosition(position) as String
                            if (selectedName != "Please select Employee Name") {
                                val role = employeeMap[selectedName]
                                role?.let {
                                    val rolePosition = roleAdapter.getPosition(it)
                                    binding.etEmployeesRole.setSelection(rolePosition)
                                }
                                binding.etEmployeesRole.visibility = View.VISIBLE

                                val salary = employeeSalaries[selectedName]
                                binding.etSalaryAmount.setText(salary)
                            } else {
                                binding.etEmployeesRole.setSelection(0)
                                binding.etEmployeesRole.visibility = View.GONE
                                binding.etSalaryAmount.setText("")
                            }
                            calculateNetPaid() // Recalculate net paid when an employee is selected
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>) {}
                    }
            } else {
                showAddEmployeesDialog()
                Toast.makeText(requireContext(), "No employees found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTextChangeListeners() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateNetPaid()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etSalaryAmount.addTextChangedListener(textWatcher)
        binding.etAnyBonus.addTextChangedListener(textWatcher)
        binding.etAnyDeduction.addTextChangedListener(textWatcher)
    }

    private fun calculateNetPaid() {
        val salary = binding.etSalaryAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
        val bonus = binding.etAnyBonus.text.toString().trim().toDoubleOrNull() ?: 0.0
        val deduction = binding.etAnyDeduction.text.toString().trim().toDoubleOrNull() ?: 0.0

        val netPaid = salary + bonus - deduction
        binding.etNetPaid.setText(netPaid.toString())
    }

    private fun showAddEmployeesDialog() {
        context?.let {
            toolBox.showConfirmationDialog(
                context = it,
                title = "No Employees are available.",
                message = "Would you like to add an Employee now?",
                positiveButtonText = "YES",
                positiveButtonAction = { toolBox.fragmentChanger(AddNewEmployees(), it) },
                negativeButtonText = "NO",
                negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), it) },
                cancelable = false
            )
        } ?: run {
            // Handle the case where context is null, e.g., log a message or handle gracefully
            Log.e("AllEmployeesFragment", "Fragment not attached to context")
        }
    }
}