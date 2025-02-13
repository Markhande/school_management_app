package com.example.schoolerp.models.responses


data class StudentFeeDetails(
    val search_specific: String,
    val class_name: String,
    val created_at: String,
    val previous_deposit_amount: String,
    val row_total_amount: String,
    val deposite_amount: String,
    val due_balance: String,
    val discount_fee: String,
    val registration_no: String
)

data class StudentFeesDetailsResponse(
    val studentFees: List<StudentFeeDetails>
)
