package com.example.schoolerp.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.AcountChartAdapter
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AcountChartData
import com.example.schoolerp.Fragments.Fragment.FeeParticular
import com.example.schoolerp.databinding.FragmentChartOfAccountBinding
import com.example.schoolerp.repository.AddaccountchartRepository
import com.example.schoolerp.repository.getAcountChartRepository
import com.example.schoolerp.viewmodel.AddchartAcountViewModel
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.GetAcountChartViewModel
import com.example.schoolerp.viewmodelfactory.AdchartAcountViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetAcountChartViewModelFactory

class ChartOfAccount : Fragment(),AcountChartAdapter.OnItemDeleteListener {

    private lateinit var binding: FragmentChartOfAccountBinding
    private lateinit var viewModelAllClassChartOfAccount: AllClassViewModel
    private lateinit var viewModel: AddchartAcountViewModel
    private lateinit var viewmodelGetChartData: GetAcountChartViewModel
    private var AcountChartDataList: MutableList<AcountChartData> =
        mutableListOf() // Make it mutable
    private lateinit var adapter: AcountChartAdapter
    val toolBox = MethodLibrary()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartOfAccountBinding.inflate(
            inflater,
            container,
            false
        )
        viewModeladdhead()
        setupLisners()
        observeViewModel()
        getSchoolId()
        setupViewModeAcountChart()
        observeChart()
        initView()
        return binding.root
    }

    private fun getSchoolId(): String {
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

    private fun setupLisners() {
        binding.btnSavehead.setOnClickListener {
            submitHead()
        }
    }

    private fun viewModeladdhead() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AddaccountchartRepository(apiService)
        val factory = AdchartAcountViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AddchartAcountViewModel::class.java)
    }

    private fun submitHead() {
        if (validateEmployeeData()) {

            val AddchartAcountData = mapOf(
                "head" to binding.txtNameofHead.text.toString().trim(),
                "account_type" to binding.spinnerHeadtype.selectedItem.toString().trim(),
                "school_id" to SchoolId().getSchoolId(requireContext())

            )
            viewModel.AddchartAcount(AddchartAcountData)
            setupViewModeAcountChart()

            binding.txtNameofHead.text?.clear()
            binding.spinnerHeadtype.setSelection(0) // Resets the spinner to the first item

            // Show the confirmation dialog
            }
    }
    private fun validateEmployeeData(): Boolean {

        val head = binding.txtNameofHead.text.toString().trim()

        return when {



            head.isEmpty() -> {
                showError(binding.txtNameofHead, "Enter Head Description")
                false
            }


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
            title = "Data Added Successfully.",
            message = "Would You Like Add More Head?",
            positiveButtonText = "YES",
            positiveButtonAction = { toolBox.fragmentChanger(ChartOfAccount(), requireContext()) },
            negativeButtonText = "NO",
            negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), requireContext())},
            cancelable = false
        )
    }
    private fun setupViewModeAcountChart() {
        val apiService = RetrofitHelper.getApiService()
        val repository = getAcountChartRepository(apiService)
        val factory = GetAcountChartViewModelFactory(repository)
        viewmodelGetChartData =
            ViewModelProvider(this, factory).get(GetAcountChartViewModel::class.java)

        val schoolId = getSchoolId()
        viewmodelGetChartData.fetchAcountChart(schoolId.trim())
    }


    private fun observeViewModel() {
        viewModel.AddaccountchartResponse.observe(viewLifecycleOwner) { response ->
            Log.d("PaySalary", "Response Message: ${response.message}")

            val messageToShow =
                response.message ?: "Income data added successfully" // Default message if null

            if (response.status) {
                showAddFeeParticularDialog()
                Toast.makeText(context, messageToShow, Toast.LENGTH_SHORT).show()

                observeChart()  // Refresh the chart data
                initView()       // Reinitialize the RecyclerView with the updated data
            } else {
                // Show failure message in Toast
                Toast.makeText(context, "Failed to add salary: $messageToShow", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun observeChart() {
        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
            return
        }

        viewmodelGetChartData.AcountChart.observe(viewLifecycleOwner, Observer { response ->
            Log.d("profitData", "Raw response: $response")

            response?.let {
                Log.d("acountData", "acountData status: ${it.status}")
                val profitData = it.data

                if (profitData != null) {
                    AcountChartDataList = profitData.toMutableList()  // Ensure it's a MutableList
                    adapter.updateData(AcountChartDataList)
                    toggleEmptyView(false)
                } else {
                    toggleEmptyView(true)
                    Log.d("acountData", "acountData is null")
                }
            } ?: run {
                Log.d("acountData", "Response is null")
                toggleEmptyView(true)
            }
        })
    }


    private fun initView() {
        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        binding.recyclerViewAcountHead.layoutManager = layoutManager

        adapter = AcountChartAdapter(AcountChartDataList, this)
        binding.recyclerViewAcountHead.adapter = adapter
    }
    private fun toggleEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.visibility = View.VISIBLE
            binding.recyclerViewAcountHead.visibility = View.GONE
        } else {
            // Show the RecyclerView and hide the "No data" text
            binding.txtForDefault.visibility = View.GONE
            binding.recyclerViewAcountHead.visibility = View.VISIBLE
        }

    }
    override fun onItemDelete(acount: AcountChartData, position: Int) {
        // Show a confirmation dialog before deleting
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete this account chart?")
            .setPositiveButton("Yes") { dialog, _ ->
                val schoolId = acount.school_id
                val Id = acount.id

                // Call ViewModel to delete the account
                viewmodelGetChartData.deleteAcountDelete(schoolId, Id)

                // Remove the item from the list and update the adapter
                AcountChartDataList.removeAt(position)
                adapter.updateData(AcountChartDataList)

                // Show a success toast
                Toast.makeText(
                    requireContext(),
                    "Account chart deleted successfully.",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss() // Close the dialog
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Close the dialog without doing anything
            }
            .create()
            .show()
    }
}