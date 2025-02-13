package com.example.schoolerp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.EmployeeAttendance
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemEmpattandanceBinding

class AttendanceForClickableStatus(
    private var employees: List<EmployeeAttendance>,
    private val onAttendanceChanged: (String, String) -> Unit // Callback for attendance updates
) : RecyclerView.Adapter<AttendanceForClickableStatus.EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmpattandanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding, onAttendanceChanged)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employees[position], position)
    }

    override fun getItemCount(): Int = employees.size

    fun updateData(newEmployees: List<EmployeeAttendance>) {
        employees = newEmployees
        notifyDataSetChanged()
    }

    fun getEmployeeNames(): List<String> {
        return employees.map { it.employee_name }
    }

    fun getEmployees(): List<EmployeeAttendance> {
        return employees
    }

    class EmployeeViewHolder(
        private val binding: ItemEmpattandanceBinding,
        private val onAttendanceChanged: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: EmployeeAttendance, position: Int) {
            binding.srNo.text = "${position + 1}."
            binding.TxtEmpName.text = employee.employee_name
            binding.TxtEmpTitle.text = employee.employee_role

            // Set alternating background colors
            val color = if (position % 2 == 0) {
                ContextCompat.getColor(itemView.context, R.color.white)
            } else {
                ContextCompat.getColor(itemView.context, R.color.gray)
            }
            binding.listItemBody.setBackgroundColor(color)

            // Set default status to "P" if null
            if (employee.status == null) {
                employee.status = "P" // Default to Present
            }

            resetButtonStyles()
            selectButtonBasedOnStatus(employee.status!!)

            // Click listeners for attendance buttons
            binding.btnPresent.setOnClickListener { updateSelection("P", employee) }
            binding.btnAbsent.setOnClickListener { updateSelection("A", employee) }
            binding.btnLeave.setOnClickListener { updateSelection("L", employee) }
        }

        private fun updateSelection(status: String, employee: EmployeeAttendance) {
            employee.status = status // Update employee's status
            resetButtonStyles()
            selectButtonBasedOnStatus(status) // Update the button styles based on the new status
            onAttendanceChanged(employee.employee_name, status)
        }

        private fun selectButtonBasedOnStatus(status: String) {
            when (status) {
                "P" -> {
                    binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selected_p)
                    binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selector)
                    binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selector)
                }
                "A" -> {
                    binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selected_a)
                    binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selector)
                    binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selector)
                }
                "L" -> {
                    binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selected_l)
                    binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selector)
                    binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selector)
                }
            }
        }

        private fun resetButtonStyles() {
            binding.btnPresent.setBackgroundResource(R.drawable.attendance_drawable_selector)
            binding.btnAbsent.setBackgroundResource(R.drawable.attendance_drawable_selector)
            binding.btnLeave.setBackgroundResource(R.drawable.attendance_drawable_selector)
        }
    }
}
