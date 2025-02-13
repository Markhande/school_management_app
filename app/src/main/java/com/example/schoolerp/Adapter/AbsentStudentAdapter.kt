package com.example.schoolerp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.AbsentStudent
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemAbsentStudentBinding

class AbsentStudentAdapter(private var students: List<AbsentStudent>) : RecyclerView.Adapter<AbsentStudentAdapter.ViewHolder>() {

    fun updateData(newData: List<AbsentStudent>) {
        students = newData
        notifyDataSetChanged() // Notifies that the data set has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAbsentStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) // Return a ViewHolder with the binding
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student) // Bind the student data to the ViewHolder
    }

    override fun getItemCount() = students.size // Return the number of items

    class ViewHolder(private val binding: ItemAbsentStudentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: AbsentStudent) {
            binding.textViewStudentName.text = student.st_name

            student.imgRes?.let {
                binding.imageViewStudent.setImageResource(it)
            } ?: run {
                binding.imageViewStudent.setImageResource(R.drawable.boynewadd)
            }
        }
    }
}
