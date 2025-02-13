package com.example.schoolerp.Fragments.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentMarkingGradingBinding
import com.example.schoolerp.repository.AddMarkGradingRepository
import com.example.schoolerp.viewmodel.AddMarkGradingViewModel
import com.example.schoolerp.viewmodelfactory.addMarkGradingViewModelFactory


class MarkingGrading : Fragment() {
    private lateinit var binding:FragmentMarkingGradingBinding
    private lateinit var viewModel: AddMarkGradingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMarkingGradingBinding.bind(inflater.inflate(R.layout.fragment_marking_grading,null))
        viewmodlemardkgrading()
        observeViewModel()
        setupListeners()
        return binding.root
    }
    private fun viewmodlemardkgrading(){
        val apiService=RetrofitHelper.getApiService()
        val repository=AddMarkGradingRepository(apiService)
        val factory=addMarkGradingViewModelFactory(repository)
        viewModel=ViewModelProvider(this,factory).get(AddMarkGradingViewModel::class.java)
    }
    private fun setupListeners() {
        binding.btnSubmitMark.setOnClickListener {

            val data = mapOf(
                "school_id" to SchoolId().getSchoolId(requireContext()),
                // "subject_id" to binding.subjectIdEditText.text.toString(),
                "marks" to binding.EditGrad.text.toString(),
                "marks" to binding.EditFrom.text.toString(),
                "marks" to binding.Editupto.text.toString(),
                "marks" to binding.EditStatus.text.toString(),

                )

            viewModel.addMarkGrading(data)
        }
    }

    private fun observeViewModel() {
        viewModel.GradingResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.success) {
                Toast.makeText(requireContext(), "Success: ${response.message}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error: ${response?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
