package com.example.schoolerp.models.responses

import com.example.schoolerp.models.requests.MessageData

data class BaseOnInputMessageResponse(
    val status: Boolean,
    val Message: String,
    val data: BaseOnInputeMessageData
)

data class BaseOnInputeMessageData(
    val classes: Any?, // Can be a String or List<Class>
    val students: Any?, // Can be a String or List<Students>
    val employees: Any?, // Can be a String or List<Employees>
    val employees_id: Any? // Can be a String or List<Employees>
)

data class Classes(
    val class_id:String,
    val class_name: String
)

data class Students(
    val  student_id :String,
    val student_name: String
)

data class Employees(
    val employee_id:String,
    val employee_name: String
)
