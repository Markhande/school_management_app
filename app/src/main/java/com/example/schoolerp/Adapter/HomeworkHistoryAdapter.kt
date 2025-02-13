package com.example.schoolerp.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemHomeworkBinding
import com.example.schoolerp.databinding.ItemHomeworkHistoryBinding
import com.example.schoolerp.models.requests.HomeworkHistory
import com.example.schoolerp.util.ImageUtil
import com.example.student.FullScreenImageActivity

class HomeworkHistoryAdapter(
    private var homeworkList: List<HomeworkHistory>
) : RecyclerView.Adapter<HomeworkHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHomeworkHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(homework: HomeworkHistory) {
            binding.tvDate.text = homework.homework_date
            binding.tvTeacher.text = homework.set_by
            binding.tvClass.text = homework.classes
            binding.tvDetail.text = homework.homework_detail
            binding.tvSubject.text = homework.subject
            val imageUrl = ImageUtil.getFullImageUrl("homework", homework.attachment)

            Glide.with(binding.ivImagee.context)
                .load(imageUrl)  // Assuming getFullImageUrl returns a valid image URL
                .into(binding.ivImagee)  // Load the image into ImageView

            // Set click listener for the ImageView to open the full-screen view
            binding.ivImagee.setOnClickListener {
                val context = binding.ivImagee.context
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("image_url", imageUrl)  // Pass the image URL to the FullScreen activity
                context.startActivity(intent)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeworkHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(homeworkList[position])
    }

    override fun getItemCount(): Int = homeworkList.size

    fun updateData(newData: List<HomeworkHistory>) {
        homeworkList = newData
        notifyDataSetChanged()
    }
    fun clearData() {
        homeworkList = emptyList()
        notifyDataSetChanged()
    }
}