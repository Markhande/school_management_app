package com.example.schoolerp.models.responses


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class StudentAttendanceReportResponse(
    var `data`: List<Data>,
    var message: String,
    var status: Boolean
) {
    @Keep
    data class Data(
        var attendance: List<Attendance>,
        var class_id: String,
        var class_name: String,
        var serial_no: Int,
        var student_id: String,
        var student_name: String
    ) {
        @Keep
        data class Attendance(
            var date: String,
            var status: String
        )
    }
}

data class Item(
    val studentName: String,
    val className: String
)


