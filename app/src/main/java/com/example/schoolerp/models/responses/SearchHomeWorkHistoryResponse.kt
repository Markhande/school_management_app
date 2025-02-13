package com.example.schoolerp.models.responses

import com.example.schoolerp.models.requests.HomeworkHistory

class SearchHomeWorkHistoryResponse(
    val status: Boolean,
    val data: List<HomeworkHistory>
)