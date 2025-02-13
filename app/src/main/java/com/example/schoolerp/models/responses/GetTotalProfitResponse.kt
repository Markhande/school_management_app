package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.TotalProfitData

data class GetTotalProfitResponse(
    val status: Boolean,
    val message: String,
    val data: TotalProfitData?
)
