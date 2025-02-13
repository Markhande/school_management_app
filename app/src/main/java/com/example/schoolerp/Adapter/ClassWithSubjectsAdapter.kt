package com.example.schoolerp.Adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.DataClasses.ClassWithSubjects
import com.example.schoolerp.Fragments.AssignSubject
import com.example.schoolerp.Fragments.UpdateClassWithSubject
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.ItemSubjectallclassBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ClassWithSubjectsAdapter(private val classList: List<ClassWithSubjects>) :
    RecyclerView.Adapter<ClassWithSubjectsAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(private val binding: ItemSubjectallclassBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(classWithSubjects: ClassWithSubjects) {

                if (!classWithSubjects.class_name.isNullOrEmpty()){
                    binding.tvClassName.text = classWithSubjects.class_name
                    binding.tvTotalSubjects.text = "${classWithSubjects.subjects.size}"
                    binding.tvTotalMarks.text = "${classWithSubjects.subjects.sumOf { it.marks.toIntOrNull() ?: 0 }}"
                    binding.IdName.text = "${classWithSubjects.subjects.sumOf { it.id.toIntOrNull() ?: 0 }}"
                    binding.pieChart.animateY(1400, Easing.EaseInOutQuad)

                    classWithSubjects.subjects.forEach { subject ->
                        val subjectTextView = TextView(binding.root.context).apply {
                            text = "${subject.subject_name}: ${subject.marks} marks"
                            textSize = 20f
                            setTextColor(Color.BLACK)
                        }
                    }
                    setupPieChart(classWithSubjects)

                    binding.iconEdit.setOnClickListener {
                        // Create a bundle to pass data to the UpdateClassWithSubject fragment
                        val bundle = Bundle().apply {
                            putString("subject_name", classWithSubjects.subjects.firstOrNull()?.subject_name ?: "")
                            putString("class_name", classWithSubjects.classes)
                            putString("class_id", classWithSubjects.class_name)
                            putString("marks", classWithSubjects.subjects.firstOrNull()?.marks ?: "")
                            putString("ClassId", classWithSubjects.subjects.firstOrNull()?.id ?: "")
                        }
                        // Navigate to the UpdateClassWithSubject fragment
                        val fragment = UpdateClassWithSubject()
                        fragment.arguments = bundle

                        // Perform the fragment transaction
                        val transaction = (binding.root.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_container, fragment)
                        transaction.addToBackStack(null)  // Optional: to keep the current fragment in the back stack
                        transaction.commit()
                    }
                }else{
                    binding.subjectAllClassLayout.visibility = View.GONE
                }
        }

        private fun setupPieChart(classWithSubjects: ClassWithSubjects) {
            val entries = ArrayList<PieEntry>()

            classWithSubjects.subjects.forEach { subject ->
                val marks = subject.marks.toFloatOrNull() ?: 0f
//                val array = arrayOf(subject.id)
//                entries.add(PieEntry(marks, subject.subject_name+" : "+subject.marks+":"+array.joinToString()))
                entries.add(PieEntry(marks, subject.subject_name))
            }


            val dataSet = PieDataSet(entries, "Subjects")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

            val data = PieData(dataSet)
            binding.pieChart.data = data
            binding.pieChart.setDrawHoleEnabled(false)
            binding.pieChart.description.isEnabled = false
            binding.pieChart.setUsePercentValues(true)
            binding.pieChart.setEntryLabelColor(Color.BLACK)
            binding.pieChart.invalidate()
        }
        fun getSubjectId(classWithSubjects: ClassWithSubjects): Array<String> {
            // Sum of marks of subjects in the class
            val sumOfMarks = classWithSubjects.subjects.sumOf { it.marks.toIntOrNull() ?: 0 }

            // Return it as an array with a string representation
            return arrayOf(sumOfMarks.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemSubjectallclassBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(classList[position])
    }

    override fun getItemCount(): Int {
        return classList.size
    }
}
