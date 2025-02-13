package com.example.schoolerp.Fragments

import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.schoolerp.Adapter.SearchEmpPrintAdapter
import com.example.schoolerp.Adapter.SearchStudentPrintAdapter
import com.example.schoolerp.Adapter.StudentFeeParticularPrintAdapter
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.DataClasses.FeeParticularData
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentStudentDetailFeesSlipBinding

class StudentDetailFeesSlip : Fragment() {
    private lateinit var binding: FragmentStudentDetailFeesSlipBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        binding = FragmentStudentDetailFeesSlipBinding.inflate(inflater, container, false)

        // Retrieve data passed to the fragment
        val feeParticularDataList = arguments?.getParcelableArrayList<FeeParticularData>("Fees")

        feeParticularDataList?.let {
            populateFields(it) // Populate UI with data
        }

        binding.btnPrint.setOnClickListener {
            printSalarySlip()
        }

        return binding.root
    }

    private fun populateFields(data: List<FeeParticularData>) {
        // Use the first item from the list to populate the fields
        val firstData = data.firstOrNull() ?: return

        binding.apply {
            etStudentName.setText(firstData.st_name)
            //etRegistrationNo.setText(firstData.id)
            spStudentSelectClass.setText(firstData.class_name)
            etPreviousDepositeAmount.setText(firstData.previous_deposit_amount)
            etDiscountAmount.setText(firstData.discount_fee)
            edtTotalamount.setText(firstData.row_total_amount)
            edtLastDepositeAmount.setText(firstData.deposite_amount)
            txtdueamount.setText(firstData.due_balance)
            etschoolId.setText(firstData.school_id)
            etregistrationNo.setText(firstData.registration_no)

        }
    }

    private fun createFeeParticularData(): FeeParticularData {
        return FeeParticularData(
            id = "/*binding.etRegistrationNo.text.toString()*/",
            classes = "binding.spStudentSelectClass.text.toString()",
            search_specific = "", // Replace with appropriate value if required
            particular_label = emptyList(), // Replace with actual label list if needed
            prefix_amount = emptyList(), // Replace with actual amount list if needed
            previous_deposit_amount = binding.etPreviousDepositeAmount.text.toString(),
            discount_fee = binding.etDiscountAmount.text.toString(),
            row_total_amount = binding.edtTotalamount.text.toString(),
            due_balance = binding.txtdueamount.text.toString().takeIf { it.isNotEmpty() },
            deposite_amount = binding.edtLastDepositeAmount.text.toString()
                .takeIf { it.isNotEmpty() },
            school_id = binding.etschoolId.text.toString(),
            created_at = "", // Replace with a dynamic value if required
            updated_at = "", // Replace with a dynamic value if required
            registration_no = binding.etregistrationNo.text.toString(), // Replace with a dynamic value if required
            class_name = binding.spStudentSelectClass.text.toString(), // Replace with a dynamic value if required
            st_name = binding.etStudentName.text.toString(),
            student_id = binding.etStudentName.text.toString(),
        )
    }

    private fun printSalarySlip() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val feeParticularDataList = arguments?.getParcelableArrayList<FeeParticularData>("Fees")
        val firstData = feeParticularDataList?.firstOrNull()

        Log.d("PrintDebug", "Printing FeeParticularData: $firstData")

        if (firstData != null) {
            val printAdapter = StudentFeeParticularPrintAdapter(requireContext(), firstData)
            printManager.print(
                "Student Fees Receipt",
                printAdapter,
                PrintAttributes.Builder().build()
            )
        } else {
            Log.e("PrintDebug", "No FeeParticularData available for printing")
        }
    }
}