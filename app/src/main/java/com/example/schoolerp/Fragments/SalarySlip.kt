package com.example.schoolerp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolerp.Adapter.SalarySlipAdapter
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.SalarySlipData
import com.example.schoolerp.Fragments.Fragment.AddStudent
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentFeeSlipBinding
import com.example.schoolerp.databinding.FragmentPaySalaryBinding
import com.example.schoolerp.databinding.FragmentSalarySlipBinding
import com.example.schoolerp.repository.GetSlipSalaryRepository
import com.example.schoolerp.viewmodel.GetSalarySlipViewModel
import com.example.schoolerp.viewmodelfactory.GetSalarySlipViewModelFactory

class SalarySlip : Fragment() {
    private lateinit var binding:FragmentSalarySlipBinding
    private lateinit var viewModel: GetSalarySlipViewModel
    private lateinit var salaryslipList: List<SalarySlipData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSalarySlipBinding.bind(inflater.inflate(R.layout.fragment_salary_slip,null))
        initview()
        setupView()
        observeSlip()

        return binding.root
    }
    private fun setupView(){
        val apiService= RetrofitHelper.getApiService()
        val repository= GetSlipSalaryRepository(apiService)
        val factory= GetSalarySlipViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(GetSalarySlipViewModel::class.java)
        val schoolId = SchoolId().getSchoolId(requireContext())
        viewModel.fetchSlip(schoolId.trim())

    }
    private fun observeSlip() {
        viewModel.SalarySlip.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                Log.d("SalarySlipData", "Data status: ${it.status}")
                val salaryDataList = it.data
                if (salaryDataList != null) {
                    salaryslipList = salaryDataList
                    setupRecyclerView(salaryslipList)
                    toggleEmptyView(false)
                } else {
                    Log.d("SalarySlipData", "Data is null")
                    toggleEmptyView(true)

                }
            } ?: run {
                Log.d("SalarySlipData", "Response is null")
                toggleEmptyView(true)

            }
        })
    }

    private fun setupRecyclerView(salaryList: List<SalarySlipData>) {
        val adapter = SalarySlipAdapter(salaryList, object : SalarySlipAdapter.SalarySlipActionListener {
            override fun onSalarySlipClicked(salaryData: SalarySlipData) {
                val bundle = Bundle().apply {
                    putParcelable("salarySlipData", salaryData)
                    putString("employee_name", salaryData.employee_name)
                }

                val salaryDetailFragment = SalaryDetailFragment().apply {
                    arguments = bundle
                }

                (requireActivity() as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, salaryDetailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        binding.searchViewSalary.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("SearchView", "Search query changed: $newText")
                adapter.filter(newText.orEmpty())
                return true
            }
        })

        binding.salarySlipRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.salarySlipRecyclerView.adapter = adapter
    }

    private fun toggleEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.visibility = View.VISIBLE
            binding.salarySlipRecyclerView.visibility = View.GONE
        } else {
            binding.txtForDefault.visibility = View.GONE
            binding.salarySlipRecyclerView.visibility = View.VISIBLE
        }

        setuplistner(isEmpty)
    }
    private fun setuplistner(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.setOnClickListener {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, PaySalary()) // Replace with your container ID
                transaction.addToBackStack(null) // Add transaction to back stack if you want to navigate back
                transaction.commit()
            }
        } else {
            binding.txtForDefault.setOnClickListener(null) // Remove the click listener if data is not empty
        }
    }

    private fun initview() {
    }
}

