package com.example.schoolerp.DataClasses

data class TotalProfitData(
    val schoolId: String,
    val totalPreviousDeposit: String,
    val totalExpenseAmount: String,
    val grand_total: Int,
    val monthExpense: String,
    val monthDeposit: String,
    val month_total: Int
)
