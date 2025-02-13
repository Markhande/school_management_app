package com.example.schoolerp.viewmodel


import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.helpers.MethodLibrary
import com.example.onboardingschool.LoginPage
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.models.responses.getStudentCheckStatusResposne
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class studentCheckStatusViewModel : ViewModel(){

    private val _studentStatus = MutableLiveData<getStudentCheckStatusResposne>()
    val studentStatus: LiveData<getStudentCheckStatusResposne> get() = _studentStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val apiService = RetrofitHelper.getApiService()

    var toolBox = MethodLibrary()

    fun getStudentStatus(context: Context,schoolId: String,studentId: String){
        apiService.getStudentCheckStatus(schoolId,studentId).enqueue(object :
            Callback<getStudentCheckStatusResposne> {
            override fun onResponse(
                p0: Call<getStudentCheckStatusResposne>,
                p1: Response<getStudentCheckStatusResposne>
            ) {
                //Toast.makeText(context, p1.body()?.student_status, Toast.LENGTH_SHORT).show()
                if (p1.body()?.student_status.equals("Inactive")){
                    toolBox.clearSharedprepared("onboarding_prefs",context)
                    toolBox.clearSharedprepared("MyPrefs",context)
                    toolBox.showConfirmationDialog(
                        context = context,
                        title = "Your account is inactive!",
                        message = "Please Contact Your School.",
                        positiveButtonText = "OK",
                        positiveButtonAction = {
                            toolBox.activityChanger(LoginPage(), context) },
                        negativeButtonText = "",
                        negativeButtonAction = { },
                        cancelable = false )
                }
            }

            override fun onFailure(p0: Call<getStudentCheckStatusResposne>, p1: Throwable) {
                Toast.makeText(context, "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
        })
    }
}