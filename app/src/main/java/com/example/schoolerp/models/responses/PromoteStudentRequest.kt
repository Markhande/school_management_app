package com.example.schoolerp.models.responses

data class PromoteStudentRequest(
    val student_ids: List<Int>, // List of selected student IDs
    val new_class: Int          // The new class to promote students to
)
