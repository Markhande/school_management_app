package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetTotalProfitResponse
import com.example.schoolerp.models.responses.GetgetCollectionStatusResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetCollectionStatusRepository(val apiService: ApiService) {
    fun getCollectionStatus(schoolId: String): LiveData<GetgetCollectionStatusResponse> {
        val result= MutableLiveData<GetgetCollectionStatusResponse>()

        apiService.getCollectionStatus(schoolId).enqueue(object : Callback<GetgetCollectionStatusResponse> {
            override fun onResponse(
                call: Call<GetgetCollectionStatusResponse>,
                response: Response<GetgetCollectionStatusResponse>
            ) {
                if (response.isSuccessful){
                    result.value=response.body()

                }else{

                }
            }

            override fun onFailure(call: Call<GetgetCollectionStatusResponse>, t: Throwable) {
                //  result.value=GetTotalProfitResponse(status = false, message = t.message ?:)
            }
        })

        return result
    }
}



