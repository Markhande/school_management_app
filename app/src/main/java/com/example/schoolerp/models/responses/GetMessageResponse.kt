package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.GetMessageData

data class GetMessageResponse(
    val status: Boolean,
    val message: String,
    val data: List<GetMessageData>
)

