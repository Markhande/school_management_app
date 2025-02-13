package com.example.schoolerp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.helpers.MethodLibrary
import com.example.schoolerp.DataClasses.TimeTableData
import com.example.schoolerp.databinding.ItemTimeTableBinding
import com.example.schoolerp.util.ImageUtil
import com.example.student.FullScreenImageActivity

class TimeTableAdapter(
    private var getTimeTableList: List<TimeTableData>,
    private val actionListener: TimeTableActionListener // Pass the listener to the adapter
) : RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {

    interface TimeTableActionListener {
        fun onEditClicked(data: TimeTableData)
        fun onDeleteClicked(data: TimeTableData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val binding =
            ItemTimeTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeTableViewHolder(binding, actionListener)
    }

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        holder.bind(getTimeTableList[position])
    }

    override fun getItemCount(): Int = getTimeTableList.size

    // Pass the listener to the ViewHolder
    class TimeTableViewHolder(
        private val binding: ItemTimeTableBinding,
        private val actionListener: TimeTableActionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TimeTableData) {
            binding.className.text = data.class_name
            val toolBox = MethodLibrary()

            val imageUrl = ImageUtil.getFullImageUrl("timetable", data.picture)

            toolBox.displayImageSquare(imageUrl, binding.additionalImageView, binding.root.context)

            binding.additionalImageView.setOnClickListener {
                val context = binding.additionalImageView.context
                val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                    putExtra("image_url", imageUrl)
                }
                context.startActivity(intent)
            }

            binding.updated.setOnClickListener {
//                val data = mapOf(
//                    "student_id" to data.picture,
//                    "role" to "admin",
//                )
//                toolBox.sendDataToFragment(binding.root.context, updateTimeTable(), data)
                actionListener.onEditClicked(data)
            }
            binding.deleted.setOnClickListener {
                actionListener.onDeleteClicked(data)
            }
        }
    }

    // Method to update data in the adapter
    fun updateData(newList: List<TimeTableData>) {
        getTimeTableList = newList
        notifyDataSetChanged()
    }
    fun getTimeTableList(): List<TimeTableData> = getTimeTableList

}
