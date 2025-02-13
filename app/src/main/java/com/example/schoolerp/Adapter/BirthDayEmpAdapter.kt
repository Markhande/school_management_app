package com.example.schoolerp.Adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.ItemEmployeeAttendanceBinding
import com.example.schoolerp.repository.AddMessageRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AddMessageViewModel
import com.example.schoolerp.viewmodelfactory.AddMessageViewModelFactory

class BirthDayEmpAdapter(private val context: Context, private var allemplist: MutableList<AllEmployee>) :
    RecyclerView.Adapter<BirthDayEmpAdapter.EmployeeViewHolder>() {

    private val viewModelSendMessage: AddMessageViewModel by lazy {
        val apiService = RetrofitHelper.getApiService()
        val repository = AddMessageRepository(apiService)
        val factory = AddMessageViewModelFactory(repository)
        ViewModelProvider(context as androidx.fragment.app.FragmentActivity, factory).get(AddMessageViewModel::class.java)
    }

    fun updateBirthdayempData(newData: List<AllEmployee>) {
        Log.d("AttendanceData", "Updating adapter with new data: $newData")
        allemplist.clear()
        allemplist.addAll(newData)
        Log.d("AttendanceData", "Adapter data size after update: ${allemplist.size}")
        notifyDataSetChanged()
    }

    class EmployeeViewHolder(private val binding: ItemEmployeeAttendanceBinding, private val context: Context, private val viewModelSendMessage: AddMessageViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(allEmployee: AllEmployee) {
            val name = allEmployee.employee_name?.takeIf { it.isNotEmpty() } ?: "Unknown"
            binding.textViewStudentName.text = name

            val imageUrl = ImageUtil.getFullImageUrl("employee", allEmployee.picture.toString())

            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.maleemployee)
                .error(R.drawable.maleemployee)
                .into(binding.imageViewStudent)

            binding.imageViewStudent.setOnClickListener {
                // Show send message dialog when employee image is clicked
                showSendMessageDialog(allEmployee)
            }
        }

        // Move showSendMessageDialog here so it can be accessed
        private fun showSendMessageDialog(employee: AllEmployee) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_send_message, null)
            val messageInput = dialogView.findViewById<EditText>(R.id.etMessage)
            val birthdayMessage = "Happy Birthday, ${employee.employee_name}! Wishing you a fantastic year ahead."
            messageInput.setText(birthdayMessage)


            val employeeId = employee.id

            AlertDialog.Builder(context)
                .setTitle("Send Birthday Message to ${employee.employee_name}")
                .setView(dialogView)
                .setPositiveButton("Send") { dialog, _ ->
                    val message = messageInput.text.toString().trim()
                    if (message.isNotEmpty()) {
                        val schoolId = SchoolId().getSchoolId(context)
                        val sendMessage = mapOf(
                            "recipient_type" to "specific_employee", // Changed to reflect the right entity
                            "search_specific" to employeeId,
                            "message" to message,
                            "school_id" to schoolId
                        )
                        viewModelSendMessage.sendMessage(sendMessage)
                        Toast.makeText(context, "Message sent successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please enter a message!", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeAttendanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmployeeViewHolder(binding, context, viewModelSendMessage)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(allemplist[position])
    }

    override fun getItemCount(): Int = allemplist.size
}
