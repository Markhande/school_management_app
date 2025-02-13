package com.example.schoolerp.DataClasses

data class AddaddPaySalaryResponse(
    val status: Boolean,
    val message: String,
    val data: SalaryDetails?
)

