package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.CalendarEvent

class GetCalanderResponse(
    val status: Boolean,
    val message: String,
    val data: List<CalendarEvent>
)