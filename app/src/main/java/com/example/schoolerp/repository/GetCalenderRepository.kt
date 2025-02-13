package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetCalanderResponse
import com.example.schoolerp.models.responses.GetgetCollectionStatusResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetCalenderRepository (private val apiService: ApiService){

    fun getCalender(schoolId: String): LiveData<GetCalanderResponse> {
        val result= MutableLiveData<GetCalanderResponse>()

        apiService.getcalender(schoolId).enqueue(object :
            Callback<GetCalanderResponse> {
            override fun onResponse(
                call: Call<GetCalanderResponse>,
                response: Response<GetCalanderResponse>
            ) {
                if (response.isSuccessful) {
                    result.value = response.body()

                } else {

                }
            }

            override fun onFailure(call: Call<GetCalanderResponse>, t: Throwable) {
                //  result.value=GetTotalProfitResponse(status = false, message = t.message ?:)
            }
        })

        return result
    }
}



