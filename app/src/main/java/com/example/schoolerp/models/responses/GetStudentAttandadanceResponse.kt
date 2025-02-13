package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.AbsentStudent

class GetStudentAttandadanceResponse (
    val status: Boolean,
    val message: String,
    val data: List<AbsentStudent>
)