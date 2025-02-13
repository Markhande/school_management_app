package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.FeeSummaryData
import com.example.schoolerp.DataClasses.RevenueItem

class GetgetCollectionStatusResponse(
    val status: Boolean,
    val Message: String,
    val data: FeeSummaryData
)

