package com.example.schoolerp.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpers.MethodLibrary
import com.example.schoolerp.DataClasses.GetMessageData
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.ItemGetmessageBinding
import com.example.schoolerp.util.ImageUtil
import com.example.student.FullScreenImageActivity
import com.example.teacher.TeacherDetails

class GetMessageAdapter(
    private var messages: MutableList<GetMessageData> = mutableListOf(),
    private val listener: OnMessageActionListener
) : RecyclerView.Adapter<GetMessageAdapter.MessageViewHolder>() {

    fun submitList(data: List<GetMessageData>) {
        messages.clear()
        messages.addAll(data)
        notifyDataSetChanged()
    }
    fun removeItemAt(position: Int) {
        if (position >= 0 && position < messages.size) {
            messages.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, messages.size)
        }
    }

    interface OnMessageActionListener {
        fun onEditMessage(message: GetMessageData)
        fun onDeleteMessage(message: GetMessageData, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding =
            ItemGetmessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position], position)
    }

    override fun getItemCount(): Int = messages.size

    class MessageViewHolder(
        private val binding: ItemGetmessageBinding,
        private val listener: OnMessageActionListener
    ) : RecyclerView.ViewHolder(binding.root) {



        fun bind(message: GetMessageData, position: Int) {
            val toolbox = MethodLibrary()
            binding.recipientType.text = message.created_at.trim().substring(0,10)
            binding.searchSpecific.text = message.search_specific
            binding.messageContent.text = message.message
            binding.messageSendBy.text = message.role
            val imageUrl = ImageUtil.getFullImageUrl("message", message.attachment.toString())
            toolbox.displayImageSquare(imageUrl, binding.attachmentImage, binding.root.context)

            binding.attachmentImage.setOnClickListener {
                toolbox.fullImageActivity(binding.attachmentImage, imageUrl, binding.root.context)
            }

            binding.editButton.setOnClickListener {
                if (TeacherDetails().getRole(binding.root.context) == message.role) {
                    listener.onEditMessage(message)
                } else if (SchoolId().getLoginRole(binding.root.context) == "Admin") {
                    listener.onEditMessage(message)
                } else if (SchoolId().getLoginRole(binding.root.context) == "Principal") {
                    if (message.role == "Teacher" || message.role == "Principal") {
                        listener.onEditMessage(message)
                    } else {
                        Toast.makeText(binding.root.context, "You can't edit this message", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "You can't edit this message", Toast.LENGTH_SHORT).show()
                }

            }

            binding.deleteButton.setOnClickListener {
                if (TeacherDetails().getRole(binding.root.context) == message.role) {
                    listener.onDeleteMessage(message, position)
                } else if (SchoolId().getLoginRole(binding.root.context) == "Admin") {
                    listener.onDeleteMessage(message, position)
                } else if (SchoolId().getLoginRole(binding.root.context) == "Principal") {
                    if (message.role == "Teacher" || message.role == "Principal") {
                        listener.onDeleteMessage(message, position)
                    } else {
                        Toast.makeText(binding.root.context, "You can't delete this message", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "You can't delete this message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
