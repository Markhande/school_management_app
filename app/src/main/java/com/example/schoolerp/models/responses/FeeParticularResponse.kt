package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.FeeParticularData

data class FeeParticularResponse(
    val status: Boolean,
    val Message: String,
    val data: List<FeeParticularData>
)

