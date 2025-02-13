package com.example.schoolerp.repository

import android.util.Log
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.DataClasses.InstituteProfileDataClass
import retrofit2.Response

class InstituteProfileRepository(private val apiService: ApiService) {

    suspend fun submitInstitute(
        school_name: String,
        tag_line: String,
        phone_number: String,
        website: String?,
        address: String,
        country: String?,
        school_id: String,


        imageLog: String,
    ): Response<InstituteProfileDataClass> {
        // Create a map to hold the field data
        val fields = mapOf(
            "school_name" to school_name,
            "tag_line" to tag_line,
            "number" to phone_number,
            "website" to website,
            "address" to address,
            "country" to country,
            "school_id" to school_id,
            "institute_logo" to imageLog
        )

        // Pass the map to the API service method
        Log.d("InstituteProfileRepository", "Submitting institute data: $fields")
        return apiService.submitInstitute(fields)
    }
}