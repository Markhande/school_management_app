package com.example.student.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpers.MethodLibrary
import com.example.schoolerp.DataClasses.GetMessageData
import com.example.schoolerp.ImageUtil.ImageUtil
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemGetmessageBinding
import com.example.schoolerp.databinding.ItemGetmessagestudentBinding
import com.example.student.FullScreenImageActivity
import com.example.student.StudentInfo

class GetStudentMeassageAdapter(
    private var messages: List<GetMessageData> = listOf(),
) : RecyclerView.Adapter<GetStudentMeassageAdapter.MessageViewHolder>() {

    // Submit a new list of messages to the adapter
    fun submitList(data: List<GetMessageData>) {
        messages = data
        notifyDataSetChanged()

    }

    // Create a new view holder for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemGetmessagestudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    // Bind the data to the view holder
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    // Return the size of the message list
    override fun getItemCount(): Int = messages.size

    // ViewHolder class to bind data to the individual list item
    class MessageViewHolder(private val binding: ItemGetmessagestudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imagePathMessage: String = "assetsNew/dist/img/message"

        // Bind the message data to the views
        fun bind(message: GetMessageData) {

            binding.recipientType.text = message.created_at.substring(0,10)
            binding.searchSpecific.text = message.search_specific
            binding.messageContent.text = message.message
            binding.messageSendBy.text = message.role
            val imageUrl = com.example.schoolerp.util.ImageUtil.getFullImageUrl("message", message.attachment.toString())

            if (message.attachment?.isNotEmpty() == true) {

                MethodLibrary().displayImageSquare(imageUrl, binding.attachmentImage, binding.attachmentImage.context)
                binding.attachmentImage.setOnClickListener {
                binding.attachmentImage.context.startActivity(Intent(binding.attachmentImage.context, FullScreenImageActivity::class.java).putExtra("image_url", imageUrl))
                }

            } else {
                // Set a placeholder image if no attachment is found
//                binding.attachmentImage.setImageResource(R.drawable.attachmentImage)
            }
        }
    }
}
