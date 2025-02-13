package com.example.schoolerp.models.requests

data class PasswordResetRequest(
    val password: String,
    val school_id: String
)