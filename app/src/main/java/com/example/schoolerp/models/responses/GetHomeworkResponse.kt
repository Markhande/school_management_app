package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.Homework

data class GetHomeworkResponse(
    val status: Boolean,
    val message: String,
    val data: List<Homework>
)
