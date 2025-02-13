package com.example.student


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentStudentFeeReceiptBinding
import com.example.schoolerp.viewmodel.StudentFeesViewModel
import com.example.student.Adapter.studentFeeReceiptAdapter

class StudentFeeReceipt : Fragment() {
    private lateinit var binding: FragmentStudentFeeReceiptBinding
    private lateinit var viewModel: StudentFeesViewModel
    val toolBox = MethodLibrary()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentFeeReceiptBinding.bind(inflater.inflate(R.layout.fragment_student_fee_receipt, null))
        viewModel = ViewModelProvider(this).get(StudentFeesViewModel::class.java)

        viewModel.studentFeesLiveData.observe(viewLifecycleOwner, Observer { studentFees ->
            if (studentFees != null) {
                binding.studentFeeReceiptList.layoutManager = LinearLayoutManager(requireContext())
                binding.studentFeeReceiptList.adapter = studentFeeReceiptAdapter(studentFees)
            }else {
                Toast.makeText(requireContext(), studentFees.toString(), Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.logic(binding)
        viewModel.setContext(requireContext())
        viewModel.fetchStudentFees(SchoolId().getSchoolId(requireContext()), StudentInfo().getStudentId(requireContext()))
        return binding.root
    }
}