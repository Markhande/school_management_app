package com.example.schoolerp.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.SalarySlipPrintAdapter
import com.example.schoolerp.DataClasses.SalarySlipData
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentSalaryDetailBinding
import com.example.schoolerp.util.ImageUtil

class SalaryDetailFragment : Fragment() {
    private lateinit var binding: FragmentSalaryDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSalaryDetailBinding.inflate(inflater, container, false)
        populateFields()

        binding.btnPrint.setOnClickListener {
            printSalarySlip()
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun populateFields() {
        val salarySlipData = arguments?.getParcelable<SalarySlipData>("salarySlipData")

        salarySlipData?.let {
            binding.txtEmployeeName.text = it.employee_name
            binding.txtType.text = it.employee_role
            binding.etDateOfReceiving.text = it.date_of_pay
            binding.etBonus.text = it.bouns
            binding.etNet.text = it.net_paid
            binding.SalaryMonth.text = it.salary_month
            binding.etDetection.text = it.deduction
           // MethodLibrary().displayImage("${ImageUtil.BASE_URL_IMAGE}employee/${it.picture}", binding.imagestEmp , binding.root.context)

        }
    }

    private fun printSalarySlip() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val salarySlipData = arguments?.getParcelable<SalarySlipData>("salarySlipData")
        val printAdapter = SalarySlipPrintAdapter(requireContext(), salarySlipData)

        printManager.print("Salary Slip", printAdapter, PrintAttributes.Builder().build())
    }
}
