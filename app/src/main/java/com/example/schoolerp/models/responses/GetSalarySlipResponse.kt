package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.SalarySlipData

class GetSalarySlipResponse(
    val status: Boolean,
    val message: String,
    val data: List<SalarySlipData>?
)
