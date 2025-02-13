package com.example.schoolerp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.Adapters.AbsentStudentAdapter
import com.example.schoolerp.DataClasses.AbsentStudent
import com.example.schoolerp.DataClasses.EmployeeAttendance
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemEmployeeAttendanceBinding


class EmployeeAttandanceAdapter(
    private var attendanceList: List<EmployeeAttendance> = mutableListOf()
) : RecyclerView.Adapter<EmployeeAttandanceAdapter.EmployeeViewHolder>() {


    fun updateData(newData: List<EmployeeAttendance>) {
        Log.d("AttendanceData", "Updating adapter with new data: $newData")


        Log.d("AttendanceData", "Adapter data size after update: ${attendanceList.size}")

        notifyDataSetChanged()
    }

    class EmployeeViewHolder(private val binding: ItemEmployeeAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employeeAttendance: EmployeeAttendance) {
            val name = employeeAttendance.employee_name?.takeIf { it.isNotEmpty() } ?: "Unknown"
            binding.textViewStudentName.text = name

            if (employeeAttendance.imgRes != null) {
                binding.imageViewStudent.setImageResource(employeeAttendance.imgRes)
            } else {
                binding.imageViewStudent.setImageResource(R.drawable.officer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeAttendanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(attendanceList[position])
    }

    override fun getItemCount(): Int = attendanceList.size
}
