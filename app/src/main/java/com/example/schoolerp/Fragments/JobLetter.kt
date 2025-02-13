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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.BirthDayEmpAdapter
import com.example.schoolerp.Adapter.JobLetterAdapater
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentJobLetterBinding
import com.example.schoolerp.repository.AllEmployeesRepository
import com.example.schoolerp.viewmodel.AllEmployeesViewModel
import com.example.schoolerp.viewmodelfactory.AllEmployeesViewModelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class JobLetter : Fragment() {
    private lateinit var binding: FragmentJobLetterBinding
    private var jobLetterList: List<AllEmployee> = emptyList()
    private var filteredJobLetterList: List<AllEmployee> = emptyList()  // For filtered list
    private lateinit var jobLetterAdapter: JobLetterAdapater
    private lateinit var viewModelAllEmployees: AllEmployeesViewModel
    val toolBox = MethodLibrary()
    private lateinit var connectivityReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentJobLetterBinding.bind(inflater.inflate(R.layout.fragment_job_letter, null))
        setupViewModelAllEmployees()
        fetchEmployees()
        initViewJobLetterEmp()
        observeJobLetterEmp()
        registerConnectivityReceiver()

        // Set up search functionality
        setupSearch()

        return binding.root
    }

    // Search functionality setup
    private fun setupSearch() {


        binding.searchViewEmployees.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterEmployeeList(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterEmployeeList(it)
                }
                return true
            }
        })

        binding.searchViewEmployees.setOnClickListener {
            binding.searchViewEmployees.isFocusable = true
            binding.searchViewEmployees.isIconified = false
            binding.searchViewEmployees.requestFocus()
        }
    }

    private fun filterEmployeeList(query: String) {
        filteredJobLetterList = jobLetterList.filter { employee ->
            employee.employee_name.contains(query, ignoreCase = true) ||
                    employee.employee_name.contains(query, ignoreCase = true)
        }

        jobLetterAdapter.updateData(filteredJobLetterList)  // Notify the adapter with filtered data
    }

    private fun initViewJobLetterEmp() {
        binding.recylerviewjobletter.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        jobLetterAdapter = JobLetterAdapater(requireContext(), filteredJobLetterList)
        binding.recylerviewjobletter.adapter = jobLetterAdapter

        // Check internet connectivity when initializing view
        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
        }
    }

    private fun observeJobLetterEmp() {
        viewModelAllEmployees.employeeResponse.observe(viewLifecycleOwner) { response ->
            if (!toolBox.isInternetAvailable(requireContext())) {
                toolBox.deviceOffLineMassage(requireContext())
                return@observe
            }

            response?.employee?.let { employees ->
                if (employees.isNotEmpty()) {
                    jobLetterList = employees  // Update the original list
                    filteredJobLetterList = jobLetterList  // Set the filtered list as the original list initially
                    jobLetterAdapter.updateData(filteredJobLetterList)  // Notify the adapter
                }
            }
        }
    }

    private fun fetchEmployees() {
        val schoolId = SchoolId().getSchoolId(requireContext())
        viewModelAllEmployees.getAllEmployees(schoolId.trim())
    }

    private fun setupViewModelAllEmployees() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AllEmployeesRepository(apiService)
        val factory = AllEmployeesViewModelFactory(repository)
        viewModelAllEmployees =
            ViewModelProvider(this, factory).get(AllEmployeesViewModel::class.java)
        observeJobLetterEmp()  // Set up observer
    }

    private fun registerConnectivityReceiver() {
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let {
                    if (isInternetAvailable(it)) {
                        observeJobLetterEmp()
                        initViewJobLetterEmp()
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

    override fun onResume() {
        super.onResume()
        if (isInternetAvailable(requireContext())) {
            observeJobLetterEmp()
            initViewJobLetterEmp()
        }
    }
}
