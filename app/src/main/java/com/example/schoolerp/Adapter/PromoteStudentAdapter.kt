package com.example.student.Adapter

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.R

class PromoteStudentAdapter(
    private var students: List<Student>,
    private val listener: OnItemClickListener,
    private val numberListener: NumberClickListener
) : RecyclerView.Adapter<PromoteStudentAdapter.ViewHolderClass>() {

    // List to track selected student IDs
    private val selectedStudentIds = mutableListOf<String>().apply {
        // Initialize with all student IDs by default
        addAll(students.map { it.id })
    }

    interface OnItemClickListener {
        fun onItemClick(item: String, position: Int)
    }

    interface NumberClickListener {
        fun onClickedNumber(item: String, position: Int, studentId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_promotestudent, parent, false)
        return ViewHolderClass(item)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val student = students[position]
        holder.NameText.text = student.st_name
        holder.srNo.text = (position + 1).toString()

        // Set checkbox checked state to true by default
        holder.checkbox.isChecked = selectedStudentIds.contains(student.id)

        // Handle the item click (for name text)
        holder.NameText.setOnClickListener {
            listener.onItemClick(student.st_name, position)
        }

        // Handle checkbox checked change
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Add student ID to the list when checked
                if (!selectedStudentIds.contains(student.id)) {
                    selectedStudentIds.add(student.id)
                }
                numberListener.onClickedNumber(student.st_name, position, student.id)
            } else {
                // Remove student ID from the list when unchecked
                selectedStudentIds.remove(student.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return students.size
    }

    // Method to get the list of selected student IDs
    fun getSelectedStudentIds(): List<String> {
        return selectedStudentIds
    }

    // Method to update the student list (if needed)
    fun updateStudents(newStudents: List<Student>) {
        students = newStudents
        // Reinitialize selectedStudentIds with all new student IDs
        selectedStudentIds.clear()
        selectedStudentIds.addAll(newStudents.map { it.id })
        notifyDataSetChanged()
    }

    // ViewHolder for each student item
    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameText: TextView = itemView.findViewById(R.id.textViewStudentName)
        val checkbox: CheckBox = itemView.findViewById(R.id.studentPromotecheckbox)
        val srNo: TextView = itemView.findViewById(R.id.srNo)
        val mainLayout: LinearLayout = itemView.findViewById(R.id.prompteStudentMainLayout)
    }
}
