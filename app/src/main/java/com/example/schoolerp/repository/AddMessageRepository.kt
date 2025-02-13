package com.example.schoolerp.repository

import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.SendMessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMessageRepository(private val apiService: ApiService) {

    fun sendMessage(data: Map<String, String>, onSuccess: (SendMessageResponse) -> Unit, onFailure: (String) -> Unit) {
        apiService.sendMessage(data).enqueue(object : Callback<SendMessageResponse> {
            override fun onResponse(
                call: Call<SendMessageResponse>,
                response: Response<SendMessageResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<SendMessageResponse>, t: Throwable) {
                onFailure(t.message ?: "Unknown error occurred")
            }
        })
    }
}
