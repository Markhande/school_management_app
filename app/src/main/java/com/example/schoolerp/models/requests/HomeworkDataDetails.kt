package com.example.schoolerp.models.requests

data class HomeworkDataDetails(
    val homework_date: String,
    val set_by: String,
    val className: String,
    val subject: String,
    val school_id: String,
    val homework_detail: String,
    val id: Int
)
