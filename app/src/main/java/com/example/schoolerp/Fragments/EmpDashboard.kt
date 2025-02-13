package com.example.schoolerp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentEmpDashboardBinding

class EmpDashboard : Fragment() {
    private lateinit var binding:FragmentEmpDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentEmpDashboardBinding.bind(inflater.inflate(R.layout.fragment_emp_dashboard,null))
        return binding.root
    }

}