package com.example.schoolerp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schoolerp.Adapter.BirthdayStudentAdapter.ViewHolder
import com.example.schoolerp.Adapters.AbsentStudentAdapter
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AbsentStudent
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemAbsentStudentBinding
import com.example.schoolerp.repository.AddMessageRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AddMessageViewModel
import com.example.schoolerp.viewmodelfactory.AddMessageViewModelFactory

class AdmetionAdapter(
    private var students: List<Student>,
) : RecyclerView.Adapter<AdmetionAdapter.ViewHolder>() {

    fun updateadmationData(newData: List<Student>) {
        students = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAbsentStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount() = students.size

    class ViewHolder(
        private val binding: ItemAbsentStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.textViewStudentName.text = student.st_name

            val imageUrl = ImageUtil.getFullImageUrl("student", student.picture.toString())

            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.student)
                .error(R.drawable.student)
                .into(binding.imageViewStudent)

            // Add click listener if needed
            binding.imageViewStudent.setOnClickListener {
                // Handle click events here
            }
        }
    }
    }