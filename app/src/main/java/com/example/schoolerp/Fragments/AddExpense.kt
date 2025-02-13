package com.example.schoolerp.Fragments


import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.AcountChartAdapterIncome
import com.example.schoolerp.R
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AcountChartData
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentAddExpenseBinding
import com.example.schoolerp.repository.AddExpenseRepository
import com.example.schoolerp.repository.getAcountChartRepository
import com.example.schoolerp.viewmodel.AddExpenseViewModel
import com.example.schoolerp.viewmodel.GetAcountChartViewModel
import com.example.schoolerp.viewmodelfactory.AddExpenseViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetAcountChartViewModelFactory
import java.util.Calendar

class AddExpense : Fragment() {
    private lateinit var binding: FragmentAddExpenseBinding
    private lateinit var adapter1: AcountChartAdapterIncome
    private lateinit var AcountChartDataList: MutableList<AcountChartData>
    val toolBox = MethodLibrary()

    private lateinit var viewmodelGetChartData: GetAcountChartViewModel

    private val viewModel: AddExpenseViewModel by viewModels {
        AddExpenseViewModelFactory(AddExpenseRepository(RetrofitHelper.getApiService()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentAddExpenseBinding.bind(inflater.inflate(R.layout.fragment_add_expense, null))

        binding.btnSubmit.setOnClickListener {
            submitExpenseData()
        }

        binding.edtDateExpense.setOnClickListener {
            showDatePickerDialog()
        }

        binding.edtExpenseDescription.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.GONE
            }
        }

        setupRecyclerView()
        setupSearchListener()
        getSchoolId()
        setupViewModelAcountChart()
        observeChart()

        return binding.root
    }

    private fun getSchoolId(): String {
        val sharedPreferences = requireActivity().getSharedPreferences(
            "onboarding_prefs",
            AppCompatActivity.MODE_PRIVATE
        )
        return sharedPreferences.getString("school_id", "defaultSchoolId") ?: "defaultSchoolId"
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)

                binding.edtDateExpense.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun submitExpenseData() {
        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
            return
        }

        if (validateEmployeeData()) {
            val expenseDate = binding.edtDateExpense.text.toString()
            val expenseDescription = binding.edtExpenseDescription.text.toString()
            val expenseAmount = binding.edtExpenseAmount.text.toString()

            val expenseData = HashMap<String, String>().apply {
                put("date_of_expense", expenseDate)
                put("expense_amount", expenseAmount)
                put("expense_description", expenseDescription)
                put("school_id", SchoolId().getSchoolId(requireContext()))


            }
            binding.edtExpenseDescription.text?.clear()
            binding.edtDateExpense.text?.clear()
            binding.edtExpenseAmount.text?.clear()
            viewModel.submitExpenseData(expenseData).observe(viewLifecycleOwner) { apiResponse ->
                if (apiResponse != null) {
                    val message = apiResponse.Message ?: "Expense added successfully"
                    if (apiResponse.status) {
                        showAddFeeParticularDialog()
                    } else {
                        Toast.makeText(
                            activity,
                            "Failed to add Expense: $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(activity, "Failed to add Expense", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun validateEmployeeData(): Boolean {
        val dateofexpnese = binding.edtDateExpense.text.toString().trim()
        val expensedescription = binding.edtExpenseDescription.text.toString().trim()
        val amount = binding.edtExpenseAmount.text.toString().trim()

        return when {
            dateofexpnese.isEmpty() -> {
                showError(binding.edtDateExpense, "Enter Date Expense")
                false
            }
            expensedescription.isEmpty() -> {
                showError(binding.edtExpenseDescription, "Enter Expense Description")
                false
            }
            amount.isEmpty() -> {
                showError(binding.edtExpenseAmount, "Enter Expense Amount")
                false
            }
            else -> true
        }
    }

    private fun showError(view: EditText, message: String) {
        view.error = message
        view.requestFocus()
    }

    private fun showAddFeeParticularDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Expense Added Successfully.",
            message = "Would You Like Add More Expense?",
            positiveButtonText = "YES",
            positiveButtonAction = { toolBox.fragmentChanger(AddExpense(), requireContext()) },
            negativeButtonText = "NO",
            negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), requireContext())},
            cancelable = false
        )
    }
    private fun setupViewModelAcountChart() {
        val apiService = RetrofitHelper.getApiService()
        val repository = getAcountChartRepository(apiService)
        val factory = GetAcountChartViewModelFactory(repository)
        viewmodelGetChartData = ViewModelProvider(this, factory).get(GetAcountChartViewModel::class.java)

        val schoolId = getSchoolId()
        viewmodelGetChartData.fetchAcountChart(schoolId.trim())
    }

    private fun observeChart() {
        viewmodelGetChartData.AcountChart.observe(viewLifecycleOwner) { response ->
            if (response?.data != null) {
                val incomeData = response.data.filter { it.account_type.equals("Expense", ignoreCase = true) }
                updateRecyclerView(incomeData)
            } else {
                updateRecyclerView(emptyList())
            }
        }
    }

    private fun updateRecyclerView(data: List<AcountChartData>) {
        AcountChartDataList.clear()
        AcountChartDataList.addAll(data)
        adapter1.notifyDataSetChanged()
        binding.recyclerView.visibility = if (data.isEmpty()) View.GONE else View.GONE
    }

    private fun setupRecyclerView() {
        AcountChartDataList = mutableListOf()
        adapter1 = AcountChartAdapterIncome(AcountChartDataList) { selectedItem ->
            binding.edtExpenseDescription.setText(selectedItem.head)
            binding.recyclerView.visibility = View.GONE
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter1
        binding.recyclerView.visibility = View.GONE
    }

    private fun setupSearchListener() {
        binding.edtExpenseDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    filterRecyclerView(query)
                    binding.recyclerView.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterRecyclerView(query: String) {
        val filteredData = AcountChartDataList.filter {
            it.head.contains(query, ignoreCase = true)
        }
        if (filteredData.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
        } else {
            updateRecyclerView(filteredData)
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
}
