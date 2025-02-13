package com.example.student.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schoolerp.DataClasses.TimeTableData
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.ItemStudentGetTimeTableBinding
import com.example.schoolerp.util.ImageUtil
import com.example.student.FullScreenImageActivity
import com.example.student.StudentInfo

class getStudentTimeTableAdapter(
    private var getTimeTableList: List<TimeTableData> // Correctly initialize the list
) : RecyclerView.Adapter<getStudentTimeTableAdapter.TimeTableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val binding =
            ItemStudentGetTimeTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeTableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        holder.bind(getTimeTableList[position])
    }

    override fun getItemCount(): Int = getTimeTableList.size

    class TimeTableViewHolder(private val binding: ItemStudentGetTimeTableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TimeTableData) {
            if (data.class_name?.trim().equals(StudentInfo().getStudentClass(binding.root.context).trim())) {
                binding.className.text = data.class_name
                binding.dateTimetable.text=data.created_at.substring(0,10)
                binding.NoData.visibility=View.GONE
                val imageUrl = ImageUtil.getFullImageUrl("timetable", data.picture)
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.imagenotfound)
                    .into(binding.classImage)
                binding.classImage.setOnClickListener {
                    val context = binding.classImage.context
                    val intent = Intent(context, FullScreenImageActivity::class.java)
                    intent.putExtra(
                        "image_url",
                        "${ImageUtil.getFullImageUrl("timetable", data.picture)}"
                    )
                    context.startActivity(intent)
                }
            }else{
                binding.NoData.visibility= View.VISIBLE
                binding.timeTableLayout.visibility = View.GONE
            }

        }
    }

    fun updateData(newList: List<TimeTableData>) {
        getTimeTableList = newList
        notifyDataSetChanged()
    }
}