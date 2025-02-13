package com.example.schoolerp.Fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentCalenderBinding
import com.example.schoolerp.repository.AddCalenderRepository
import com.example.schoolerp.repository.getAcountChartRepository
import com.example.schoolerp.viewmodel.AddCalenderViewModel
import com.example.schoolerp.viewmodelfactory.AddCalenderViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetAcountChartViewModelFactory
import java.util.Calendar

class calender : Fragment() {
    private lateinit var binding: FragmentCalenderBinding
    private lateinit var viewmodle: AddCalenderViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalenderBinding.bind(inflater.inflate(R.layout.fragment_calender, null))

        setupviewmdoel()
        setupLisners()
        return binding.root
    }

    private fun setupviewmdoel() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AddCalenderRepository(apiService)
        val factory = AddCalenderViewModelFactory(repository)
        viewmodle = ViewModelProvider(this, factory).get(AddCalenderViewModel::class.java)
    }

    private fun setupLisners() {
        binding.btnSubmit.setOnClickListener {
            submitCalendarData()

        }
        binding.edtDate.setOnClickListener {
            showDatePickerDialog()
        }


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


    private fun submitCalendarData() {
        if (validateEmployeeData()) {

            // Retrieve values from the form
            val descrptions = binding.edtdescrptions.text.toString().trim()
            val date = binding.edtDate.text.toString().trim()
            val eventype = binding.editspinner.selectedItem.toString().trim()
            val schoolId = SchoolId().getSchoolId(requireContext())


            // Create a data map for submission
            val calendarData = mapOf(
                "description" to descrptions,
                "type" to eventype,
                "date" to date,
                "school_id" to schoolId
            )

            // Call ViewModel function to submit data
            viewmodle.addCalander(calendarData)

            // Clear fields after successful submission
            binding.edtdescrptions.text?.clear()
            binding.editspinner.setSelection(0) // Reset the spinner

            Toast.makeText(
                requireContext(),
                "Calendar data submitted successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validateEmployeeData(): Boolean {
        val date = binding.edtDate.text.toString().trim()
        val description = binding.edtdescrptions.text.toString().trim()
        val type = binding.editspinner.selectedItem.toString().trim()

        return when {
            date.isEmpty() -> {
                showError(binding.edtDate, "Enter Date Event")
                false
            }

            description.isEmpty() -> {
                showError(binding.edtdescrptions, "Enter Description Event")
                false
            }

            type == "Select Type" -> { // Assuming first item in Spinner is a placeholder like "Select Type"
                showSpinnerError("Please select a valid Type")
                false
            }

            else -> true
        }
    }

    private fun showError(view: EditText, message: String) {
        view.error = message
        view.requestFocus()
    }
    private fun showSpinnerError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
