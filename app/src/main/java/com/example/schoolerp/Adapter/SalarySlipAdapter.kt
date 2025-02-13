package com.example.schoolerp.Adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.SalarySlipData
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemSalarySlipBinding

class SalarySlipAdapter(
    private val salaryList: List<SalarySlipData>,
    private val listener: SalarySlipActionListener
) : RecyclerView.Adapter<SalarySlipAdapter.SalarySlipViewHolder>() {

    private var filteredList = ArrayList(salaryList)  // Store filtered list

    interface SalarySlipActionListener {
        fun onSalarySlipClicked(salaryData: SalarySlipData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalarySlipViewHolder {
        val binding = ItemSalarySlipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalarySlipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalarySlipViewHolder, position: Int) {
        val salaryData = filteredList[position]
        holder.bind(salaryData, position)
    }

    override fun getItemCount(): Int = filteredList.size

    inner class SalarySlipViewHolder(private val binding: ItemSalarySlipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(salaryData: SalarySlipData, position: Int) {

            binding.srNo.text = "${position + 1}."

            binding.employeeName.text = salaryData.employee_name
            binding.employeeRole.text = salaryData.employee_role

            // Alternate background colors for even and odd rows
            val backgroundColor = if (position % 2 == 0) {
                ContextCompat.getColor(binding.root.context, R.color.info_bg)
            } else {
                ContextCompat.getColor(binding.root.context, android.R.color.white)
            }

            binding.root.setBackgroundColor(backgroundColor)

            // Set click listener for item
            binding.root.setOnClickListener {
                listener.onSalarySlipClicked(salaryData)
            }
        }
    }

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(salaryList)  // Reset filtered list when query is empty
        } else {
            filteredList.addAll(salaryList.filter {
                it.employee_name!!.contains(query, ignoreCase = true)  // Filter based on employee name
            })
        }
        notifyDataSetChanged()  // Notify that the dataset has changed
    }
}
