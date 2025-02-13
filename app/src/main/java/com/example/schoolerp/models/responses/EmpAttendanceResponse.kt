package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.EmpAttendanceData

class EmpAttendanceResponse (
  val status: Boolean,
  val message: String,
  val data: EmpAttendanceData
)