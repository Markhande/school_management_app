package com.example.schoolerp.DataClasses

data class FeeSummaryData(
    val total_row_amount: Int,
    val total_deposit: Int,
    val remaining_balance: Int,
    val collection_percentage: Double,
    val remaining_percentage: Double
)
