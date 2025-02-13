package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.AddCalander
import com.example.schoolerp.models.responses.AddMarkGradingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCalenderRepository(private val apiService: ApiService) {


    fun addcalander(clanderdata: Map<String, String>): LiveData<AddCalander> {
        val result = MutableLiveData<AddCalander>()

        apiService.addcalander(clanderdata).enqueue(object :
            Callback<AddCalander> {
            override fun onResponse(call: Call<AddCalander>, response: Response<AddCalander>) {
                if (response.isSuccessful && response.body() != null) {
                    result.value = response.body()
                } else {

                }
            }

            override fun onFailure(call: Call<AddCalander>, t: Throwable) {

            }
        })

        return result
    }

}