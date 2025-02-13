package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.SearchHomeWorkHistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchHistoryHomeworkRepository(private val apiService: ApiService) {
    fun searchHomeWorkHistory(searchParams: Map<String, String>): LiveData<Result<SearchHomeWorkHistoryResponse>> {
        val liveData = MutableLiveData<Result<SearchHomeWorkHistoryResponse>>()
        apiService.searchHomeWorkHistory(searchParams).enqueue(object : Callback<SearchHomeWorkHistoryResponse> {
            override fun onResponse(call: Call<SearchHomeWorkHistoryResponse>, response: Response<SearchHomeWorkHistoryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        liveData.postValue(Result.success(it))
                    } ?: run {
                        liveData.postValue(Result.failure(Throwable("Empty response")))
                    }
                } else {
                    liveData.postValue(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<SearchHomeWorkHistoryResponse>, t: Throwable) {
                liveData.postValue(Result.failure(t))
            }
        })
        return liveData
    }
}
