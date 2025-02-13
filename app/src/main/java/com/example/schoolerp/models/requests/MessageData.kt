package com.example.schoolerp.models.requests

data class MessageData(
    val recipient_type: String,
    val search_specific: String,
    val message: String,
    val attachment: String,
    val school_id: String,
    val created_at: String?,
    val id: Int
)
