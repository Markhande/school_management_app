package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.AcountChartData

class GetAcountChartResponse(
    val status: Boolean,
    val message: String,
    val data: List<AcountChartData>?
)
