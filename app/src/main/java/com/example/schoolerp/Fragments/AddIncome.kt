package com.example.schoolerp.Fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.AcountChartAdapterIncome
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AcountChartData
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentAddIncomeBinding
import com.example.schoolerp.repository.AddIncomeRepository
import com.example.schoolerp.repository.getAcountChartRepository
import com.example.schoolerp.viewmodelfactory.AddIncomeViewModelFactory
import com.example.schoolerp.viewmodel.AddIncomeViewModel
import com.example.schoolerp.viewmodel.GetAcountChartViewModel
import com.example.schoolerp.viewmodelfactory.GetAcountChartViewModelFactory
import java.util.*

class AddIncome : Fragment() {
    private lateinit var binding: FragmentAddIncomeBinding
    private lateinit var viewmodelGetChartData: GetAcountChartViewModel
    private lateinit var adapter1: AcountChartAdapterIncome
    private lateinit var AcountChartDataList: MutableList<AcountChartData>
    val toolBox = MethodLibrary()

    private val viewModel: AddIncomeViewModel by viewModels {
        AddIncomeViewModelFactory(AddIncomeRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddIncomeBinding.bind(inflater.inflate(R.layout.fragment_add_income, null))

        binding.btnCreate.setOnClickListener {
            submitIncomeData()
        }

        binding.edtDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.edtincomedescrption.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.GONE
            }
        }

        setupSearchListener()
        getSchoolId()
        setupViewModelAcountChart()
        observeChart()
        setupRecyclerView()
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

                binding.edtDate.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun submitIncomeData() {
        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
            return
        }

        if (validateEmployeeData()) {
            val incomeDate = binding.edtDate.text.toString()
            val incomeDescription = binding.edtincomedescrption.text.toString()
            val incomeAmount = binding.edtincomeamount.text.toString()

            val incomeData = hashMapOf(
                "date_of_income" to incomeDate,
                "income_amount" to incomeAmount,
                "income_description" to incomeDescription,
                "school_id" to SchoolId().getSchoolId(requireContext())
            )
            binding.edtincomedescrption.text?.clear()
            binding.edtDate.text?.clear()
            binding.edtincomeamount.text?.clear() // Resets the spinner to the first item

            viewModel.submitIncomeData(incomeData).observe(viewLifecycleOwner) { apiResponse ->
                val message = apiResponse?.Message ?: "Income added successfully"
                if (apiResponse?.status == true) {
                    showAddFeeParticularDialog()
                } else {
                    Toast.makeText(activity, "Failed to add income: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun validateEmployeeData(): Boolean {
        val dateofexpense = binding.edtDate.text.toString().trim()
        val expensedescription = binding.edtincomedescrption.text.toString().trim()
        val amount = binding.edtincomeamount.text.toString().trim()

        return when {
            dateofexpense.isEmpty() -> {
                showError(binding.edtDate, "Enter Date of Income")
                false
            }
            expensedescription.isEmpty() -> {
                showError(binding.edtincomedescrption, "Enter Income Description")
                false
            }
            amount.isEmpty() -> {
                showError(binding.edtincomeamount, "Enter Income Amount")
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
            title = "Income Added Successfully.",
            message = "Would You Like Add More Income?",
            positiveButtonText = "YES",
            positiveButtonAction = { toolBox.fragmentChanger(AddIncome(), requireContext()) },
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
                val incomeData = response.data.filter { it.account_type.equals("Income", ignoreCase = true) }
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
            binding.edtincomedescrption.setText(selectedItem.head)
            binding.recyclerView.visibility = View.GONE
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter1
        binding.recyclerView.visibility = View.GONE
    }

    private fun setupSearchListener() {
//        binding.edtincomedescrption.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val query = s.toString().trim()
//                if (query.isNotEmpty()) {
//                    filterRecyclerView(query)
//                    binding.recyclerView.visibility = View.VISIBLE
//                } else {
//                    binding.recyclerView.visibility = View.GONE
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//        })
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
