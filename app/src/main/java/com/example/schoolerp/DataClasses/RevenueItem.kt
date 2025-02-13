package com.example.schoolerp.DataClasses

data class RevenueItem(
    val school_id: String,
    val total_previous_deposit: String,
    val total_income_amount: String,
    val revenue_total: Int,
    val month_income: String,
    val month_deposit: String,
    val month_total: Int
)
