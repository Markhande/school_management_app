package com.example.schoolerp.DataClasses

data class CalendarEvent(
    val id: String,
    val date: String,
    val description: String,
    val type: String,
    val school_id: String
)
