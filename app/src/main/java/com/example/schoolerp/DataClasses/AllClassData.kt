package com.example.schoolerp.DataClasses

import com.google.gson.annotations.SerializedName

class AllClassData (
    @SerializedName("class_name") var class_name: String? = null,
    var total_students: Int? = null,
    var total_girls: Int? = null,
    var total_boys: Int? = null,
)