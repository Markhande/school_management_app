package com.example.schoolerp.models.responses


data class ClassAndStudentGetTuitionResponse(
    val status: String,
    val data: TuitionFeeData
)

data class TuitionFeeData(
    val class_name_tuition_fees: String,
    val student_class_tuition_fees: String
)
