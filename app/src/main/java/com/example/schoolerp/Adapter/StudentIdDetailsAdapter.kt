package com.example.schoolerp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpers.MethodLibrary
import com.example.schoolerp.ImageUtil.ImageUtil
import com.example.schoolerp.R
import com.example.schoolerp.models.responses.StudentIdCardDetails

class StudentIdDetailsAdapter(private val studentList: List<StudentIdCardDetails>) :
    RecyclerView.Adapter<StudentIdDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.studentName)
        val studentId: TextView = itemView.findViewById(R.id.studentId)
        val studentDob: TextView = itemView.findViewById(R.id.studentDob)
        val studentAddress: TextView = itemView.findViewById(R.id.studentAddress)
        val studentImage: ImageView = itemView.findViewById(R.id.studentImage)
        val context = itemView.context

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.icard_layout, parent, false) // Assuming you have item_student_idcard.xml
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = studentList[position]
        val StudentImage: String = "assetsNew/img/student/"
        holder.studentName.text = student.st_name
        holder.studentId.text = student.id
        holder.studentDob.text = student.dt_of_birth ?: "Not Available"
        holder.studentAddress.text = student.address
        var ab = holder.studentImage
        holder.context

        MethodLibrary().displayImageSquare(
            "${ImageUtil.BASE_URL_IMAGE}$StudentImage${student.picture}" ,
            ab,
            holder.context
        )
    }

    override fun getItemCount(): Int {
        return studentList.size
    }


}


