package com.example.student.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.DataClasses.Homework
import com.example.schoolerp.ImageUtil.ImageUtil
import com.example.schoolerp.R
import com.example.schoolerp.databinding.StudentHomeworkItemsBinding
import com.example.student.FullScreenImageActivity
import com.example.student.StudentInfo

class getStudentHomeWorkAdapter(
    private val context: Context,
    private val homeworkList: List<Homework>,
    private val onEditClick: (Homework) -> Unit,
    private val onDeleteClick: (Homework) -> Unit
) : RecyclerView.Adapter<getStudentHomeWorkAdapter.HomeworkViewHolder>() {

    class HomeworkViewHolder(private val binding: StudentHomeworkItemsBinding) : RecyclerView.ViewHolder(binding.root) {

        val imagePathHomework: String = "assetsNew/img/homework/"
        val toolBox=MethodLibrary()
        fun bind(homework: Homework, context: Context) {


            val context = binding.root.context


            try {
                if (StudentInfo().getStudentClass(context).trim().equals(homework.class_name.trim())) {
                    binding.layoutHomeWork.visibility = View.VISIBLE
                    binding.tvgetHomeworkTeacher.text = "${homework.set_by}"
                    binding.tvgetHomeworkClass.text = "${homework.class_name}"
                    binding.tvgetHomeworkDate.text = "${homework.homework_date}"
                    binding.tvgetHomeworkSubject.text = "${homework.subject_name}"
                    binding.tvgetHomeworkDetail.text = homework.homework_detail

                    if (homework.attachment?.isNotEmpty() == true) {
                        Glide.with(binding.imgShowHomework.context)
                            .load("${ImageUtil.baseUrl}$imagePathHomework${homework.attachment}")
                            .placeholder(R.drawable.imagenotfound)
                            .into(binding.imgShowHomework)

                        binding.imgShowHomework.setOnClickListener {
                            val context = binding.imgShowHomework.context
                            val intent = Intent(context, FullScreenImageActivity::class.java)
                            intent.putExtra("image_url", "${ImageUtil.baseUrl}$imagePathHomework${homework.attachment}")
                            context.startActivity(intent)
                        }

                    } else {
                        // Set a placeholder image if no attachment is found
//                    binding.imgShowHomework.setImageResource(R.drawable.attachmentImage)
                    }

                } else {
                    binding.layoutHomeWork.visibility = View.GONE
                }

            }catch (e: Exception){
                toolBox.showConfirmationDialog(
                    context = context,
                    title = "Warning !",
                    message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                    positiveButtonText = "OK",
                    positiveButtonAction = { toolBox.activityChanger(MainActivity(), context) },
                    negativeButtonText = "",
                    negativeButtonAction = { },
                    cancelable = false
                )
            }
        }
    }

    // Inflate the view and create the ViewHolder with ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkViewHolder {
        val binding = StudentHomeworkItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeworkViewHolder(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: HomeworkViewHolder, position: Int) {
        val homework = homeworkList[position]
        holder.bind(homework, context)
    }

    // Return the total number of items in the list
    override fun getItemCount(): Int {
        return homeworkList.size
    }


}
