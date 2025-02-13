package com.example.schoolerp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.ItemAbsentStudentBinding
import com.example.schoolerp.repository.AddMessageRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AddMessageViewModel
import com.example.schoolerp.viewmodelfactory.AddMessageViewModelFactory

class BirthdayStudentAdapter(
    private var students: List<Student>,
    private val context: Context
) : RecyclerView.Adapter<BirthdayStudentAdapter.ViewHolder>() {

    private val viewModelSendMessage: AddMessageViewModel by lazy {
        val apiService = RetrofitHelper.getApiService()
        val repository = AddMessageRepository(apiService)
        val factory = AddMessageViewModelFactory(repository)
        ViewModelProvider(context as androidx.fragment.app.FragmentActivity, factory).get(AddMessageViewModel::class.java)
    }

    fun updateBirthdayData(newData: List<Student>) {
        students = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAbsentStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, viewModelSendMessage, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount() = students.size

    class ViewHolder(
        private val binding: ItemAbsentStudentBinding,
        private val viewModelSendMessage: AddMessageViewModel,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.textViewStudentName.text = student.st_name

            val imageUrl = ImageUtil.getFullImageUrl("student", student.picture.toString())

            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.student)
                .error(R.drawable.student)
                .into(binding.imageViewStudent)

            binding.imageViewStudent.setOnClickListener {
                showSendMessageDialog(student)
            }
        }

        private fun showSendMessageDialog(student: Student) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_send_message, null)
            val messageInput = dialogView.findViewById<EditText>(R.id.etMessage)
          //  val attachmentImageView = dialogView.findViewById<ImageView>(R.id.ivAttachment)

            var Id = student.id
            val birthdayMessage = "Happy Birthday, ${student.st_name}!Wishing you a fantastic year ahead."
            messageInput.setText(birthdayMessage)

            AlertDialog.Builder(context)
                .setTitle("Send Birthday Message to ${student.st_name}")
                .setView(dialogView)
                .setPositiveButton("Send") { dialog, _ ->
                    val message = messageInput.text.toString().trim()
                    if (message.isNotEmpty()) {
                        val schoolId = SchoolId().getSchoolId(context)
                        val sendMessage = mapOf(
                            "recipient_type" to "specific_student",
                            "search_specific" to Id,
                            "message" to message,
                           // "attachment" to (attachmentBase64 ?: ""),
                            "school_id" to schoolId
                        )
                        viewModelSendMessage.sendMessage(sendMessage)
                        Toast.makeText(context, "Message sent successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please enter a message!", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

}
