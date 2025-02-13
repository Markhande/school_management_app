package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.OverViewData

class GetOverViewResponse (
    val status: Boolean,
val message: String,
val data: List<OverViewData>
)
