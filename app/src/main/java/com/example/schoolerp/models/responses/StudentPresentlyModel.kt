package com.example.schoolerp.models.responses

data class StudentPresentlyModel(
    val status: Boolean,
    val message: String,
    val data: AttendanceData
)

data class AttendanceData(
    val summary: Summary,
    val monthly_details: MonthlyDetails,
    val today_status: TodayStatus,
    val yesterday_status: YesterdayStatus
)

data class Summary(
    val student_id: String,
    val employee_id: String,
    val Present: String,
    val Absent: String,
    val Leaves: String,
    val Total: Int,
    val Present_Percentage: String,
    val Absent_Percentage: String,
    val Leave_Percentage: String
)

data class MonthlyDetails(
    val Present: String,
    val Absent: String,
    val Leaves: String
)

data class TodayStatus(
    val today_status: String,
    val status: String
)

data class YesterdayStatus(
    val yesterday_status: String,
    val status: String
)

