package com.example.schoolerp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.schoolerp.databinding.FragmentTimeTableBinding
import com.example.schoolerp.util.CircularProgressViewPresent


class TimeTable : Fragment() {
    private lateinit var binding: FragmentTimeTableBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimeTableBinding.inflate(inflater, container, false)
        return binding.root
    }
}
