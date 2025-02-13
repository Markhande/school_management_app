package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.TimeTableData

data class GetTimeTableResponse(
    val status: Boolean,
    val message: String,
    val data: List<TimeTableData>
)
