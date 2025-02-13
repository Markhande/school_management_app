package com.example.schoolerp.DataClasses

data class BankStatementData(
    val date: String,
    val description: String,
    val debit: String,
    val credit: String,
    val netBalance: String
)
