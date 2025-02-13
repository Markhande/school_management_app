package com.example.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentStudentAccountSettingBinding
import com.example.schoolerp.databinding.FragmentStudentReportCardBinding

class StudentReportCard : Fragment() {
    private lateinit var binding: FragmentStudentReportCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentReportCardBinding.bind(inflater.inflate(R.layout.fragment_student_report_card, null))
        //do code here//
        return binding.root
    }


}