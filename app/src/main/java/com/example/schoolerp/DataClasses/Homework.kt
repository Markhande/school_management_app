package com.example.schoolerp.DataClasses

data  class Homework(
    val id: String,
    var homework_date: String,
    var set_by: String,
    var classes: String,
    var subject: String,
    var homework_detail: String,
    val school_id: String,
    val class_name: String,
    val subject_name: String,
    val attachment: String,
    val created_at: String,
)