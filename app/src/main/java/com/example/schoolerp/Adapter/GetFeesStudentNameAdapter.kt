package com.example.schoolerp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.R

class GetFeesStudentNameAdapter(
    private var studentList: List<Student>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<GetFeesStudentNameAdapter.StudentNameViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(student: Student)
    }

    inner class StudentNameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentNameTextView: TextView = itemView.findViewById(R.id.textViewStudentName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(studentList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentNameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_getfeesstudent_name, parent, false)
        return StudentNameViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentNameViewHolder, position: Int) {
        val student = studentList[position]

        // Bind data to the TextView
        holder.studentNameTextView.text = student.st_name

        // Alternate background colors for even and odd rows
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context,R.color.info_bg)
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.white)
            )
        }
    }
    override fun getItemCount(): Int = studentList.size

    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }
}
