package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.EmployeeAndPrinciple

data class GetTeacherPrincipleResponse(
    val status: Boolean,
    val data: List<EmployeeAndPrinciple>
)
