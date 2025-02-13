package com.example.schoolerp.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schoolerp.DataClasses.Homework
import com.example.schoolerp.R
import com.example.schoolerp.util.ImageUtil
import com.example.student.FullScreenImageActivity

class HomeworkAdapter(
    private val homeworkList: List<Homework>,
    private val onEditClick: (Homework) -> Unit,
    private val onDeleteClick: (Homework) -> Unit
) : RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder>() {

    class HomeworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teacher = itemView.findViewById<TextView>(R.id.tvTeacher)
        val className = itemView.findViewById<TextView>(R.id.tvClass)
        val date = itemView.findViewById<TextView>(R.id.tvDate)
        val subject = itemView.findViewById<TextView>(R.id.tvSubject)
        val description = itemView.findViewById<TextView>(R.id.tvDescription)
        val detail = itemView.findViewById<TextView>(R.id.tvDetail)
        val editIcon = itemView.findViewById<ImageView>(R.id.ivEdit)
        val deleteIcon = itemView.findViewById<ImageView>(R.id.ivDelete)
        val Image = itemView.findViewById<ImageView>(R.id.ivImagee)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_homework, parent, false)
        return HomeworkViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeworkViewHolder, position: Int) {
        val homework = homeworkList[position]

        // Set the text fields
        holder.teacher.text = "${homework.set_by}"
        holder.className.text = "${homework.class_name}"
        holder.date.text = "${homework.homework_date}"
        holder.subject.text = "${homework.subject_name}"
        holder.detail.text = homework.homework_detail

        val imageUrl = ImageUtil.getFullImageUrl("homework", homework.attachment)

        Glide.with(holder.itemView.context)
            .load(imageUrl)  // Assuming getFullImageUrl returns a valid image URL
            .into(holder.Image)  // Load the image into ImageView

        // Set click listener for the ImageView to open the full-screen view
        holder.Image.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", imageUrl)  // Pass the image URL to the FullScreen activity
            context.startActivity(intent)
        }

        // Handle edit and delete actions
        holder.editIcon.setOnClickListener {
            onEditClick(homework)
        }

        holder.deleteIcon.setOnClickListener {
            //Toast.makeText(holder.itemView.context, "Deletion Clicked", Toast.LENGTH_SHORT).show()
            onDeleteClick(homework)
        }

    }

    override fun getItemCount(): Int {
        return homeworkList.size
    }
}