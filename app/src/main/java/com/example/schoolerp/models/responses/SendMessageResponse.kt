package com.example.schoolerp.models.responses

import com.example.schoolerp.models.requests.MessageData

data class SendMessageResponse(
    val status: Boolean,
    val Message: String,
    val data: MessageData?
)

