package com.example.schoolerp.models.responses

import com.example.schoolerp.models.requests.HomeworkDataDetails

data class HomeWorkResponse(
    val status: Boolean,
    val message: String,
    val data: HomeworkDataDetails
)