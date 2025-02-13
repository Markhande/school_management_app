package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.RevenueItem

data class GetgetRevenueResponse(
    val status: Boolean,
    val message: String,
    val data: List<RevenueItem>? // Notice it's a list
)
