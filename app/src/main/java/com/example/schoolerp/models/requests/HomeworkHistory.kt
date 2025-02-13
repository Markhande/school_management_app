package com.example.schoolerp.models.requests

data class HomeworkHistory(
    val id: String,
    val homework_date: String,
    val set_by: String,
    val classes: String,
    val subject: String?,
    val homework_detail: String,
    val school_id: String,
    val attachment: String,
    val created_at: String
)
