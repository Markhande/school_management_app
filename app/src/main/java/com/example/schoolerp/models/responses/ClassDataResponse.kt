package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.ClassItem

class ClassDataResponse(
    val status: Boolean,
    val message: String,
     val data: List<ClassItem>
)