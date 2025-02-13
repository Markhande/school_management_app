package com.example.schoolerp.Adapter

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemEmpattandanceBinding

class AddEmpAttandanceAdapter(
    private var employees: List<AllEmployee>,
    private val onAttendanceChanged: (String, String) -> Unit
) : RecyclerView.Adapter<AddEmpAttandanceAdapter.EmployeeViewHolder>() {

    fun getEmployees(): List<AllEmployee> {
        return employees
    }

    fun getEmployeeNames(): List<String> {
        return employees.map { it.employee_name }
    }

    fun getEmployeeID(): List<String> {
        return employees.map { it.employee_id }
    }


    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnPresent: TextView = itemView.findViewById(R.id.btnPresent)
        val btnAbsent: TextView = itemView.findViewById(R.id.btnAbsent)
        val btnLeave: TextView = itemView.findViewById(R.id.btnLeave)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.listItemBody)
        val defaultColor = ContextCompat.getColor(itemView.context, android.R.color.transparent)
        val textColor = ContextCompat.getColor(itemView.context, R.color.black)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmpattandanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding, onAttendanceChanged)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {

        val employee = employees[position]
        holder.bind(employee,position)
    }

    override fun getItemCount(): Int = employees.size

    fun updateData(newEmployees: List<AllEmployee>) {
        employees = newEmployees
        notifyDataSetChanged()
    }



    class EmployeeViewHolder(
        private val binding: ItemEmpattandanceBinding,
        private val onAttendanceChanged: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: AllEmployee, position: Int) {

            binding.srNo.text = "${position + 1}."

            binding.TxtEmpName.text = employee.employee_name
            binding.TxtEmpTitle.text = employee.employee_role


            val color = if (position % 2 == 0) {
                ContextCompat.getColor(itemView.context, R.color.white)
            } else {
                ContextCompat.getColor(itemView.context, R.color.info_bg)
            }


            binding.listItemBody.setBackgroundColor(color)
            resetButtonStyles()
            // Set attendance buttons logic
            binding.btnPresent.setOnClickListener {
                resetButtonStyles()
//
                binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selected_p)
                binding.btnPresent.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.btnPresent.setTypeface(null, Typeface.BOLD)
                binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selector)
                binding.btnAbsent.setTypeface(null, Typeface.NORMAL)
                binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selector)
                binding.btnLeave.setTypeface(null, Typeface.NORMAL)
                onAttendanceChanged(employee.employee_name, "P")



            }
            binding.btnAbsent.setOnClickListener {
                resetButtonStyles()
                binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selected_a)
                binding.btnAbsent.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.btnAbsent.setTypeface(null, Typeface.BOLD)
                binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selector)
                binding.btnPresent.setTypeface(null, Typeface.NORMAL)
                binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selector)
                binding.btnLeave.setTypeface(null, Typeface.NORMAL)
                onAttendanceChanged(employee.employee_name, "A")

            }
            binding.btnLeave.setOnClickListener {
                resetButtonStyles()
                binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selected_l)
                binding.btnLeave.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.btnLeave.setTypeface(null, Typeface.BOLD)
                val textColor = ContextCompat.getColor(itemView.context, R.color.black)
                binding.btnAbsent.setTextColor(textColor)
                binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selector)
                binding.btnPresent.setTypeface(null, Typeface.NORMAL)
                binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selector)
                binding.btnAbsent.setTypeface(null, Typeface.NORMAL)
                onAttendanceChanged(employee.employee_name, "L")

            }
        }
        // Helper function to reset styles
        private fun resetButtonStyles() {
            val textColor = ContextCompat.getColor(itemView.context, R.color.black)
            binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selected_p)
            binding.btnPresent.setTextColor(textColor)
            binding.btnAbsent.setTextColor(textColor)
            binding.btnLeave.setTextColor(textColor)
        }

        // Default to "Absent" (A) when the item is created
//            binding.radioGroupStatus.check(binding.radioButtonA.id)
//
//            binding.radioGroupStatus.setOnCheckedChangeListener { _, checkedId ->
//                val status = when (checkedId) {
//                    binding.radioButtonA.id -> "P"  // Absent
//                    binding.radioButtonB.id -> "A"  // Present
//                    binding.radioButtonC.id -> "L"  // Presenthttps://github.com/KishorTrucksvilla/e_School_ERP.git

//
//                    else -> ""
//                }
//                // Call the callback to update the attendanceMap in the Fragment
//                onAttendanceChanged(employee.employee_name, status)
//            }
    }
}
