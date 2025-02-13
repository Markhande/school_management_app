package com.example.schoolerp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.Fragments.AllClass
import com.example.schoolerp.Fragments.NewClass
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentStudentFeeReceiptBinding
import com.example.schoolerp.models.responses.StudentFeeDetails
import com.example.student.StudentFeeReceipt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentFeesViewModel(application: Application) : AndroidViewModel(application) {
    private val _studentFeesLiveData = MutableLiveData<List<StudentFeeDetails>>()
    private lateinit var binding: FragmentStudentFeeReceiptBinding
    var toolBox = MethodLibrary()
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    fun setContext(context: Context) {
        this.context = context

    }

    fun logic(binding: FragmentStudentFeeReceiptBinding) {
        this.binding = binding
        //binding.testMe.text =  "Student Fees"
    }


    val studentFeesLiveData: LiveData<List<StudentFeeDetails>> get() = _studentFeesLiveData

    private val apiService = RetrofitHelper.getApiService()

    fun fetchStudentFees(schoolId: String, studentId: String) {
        if (!toolBox.isInternetAvailable(context)) {
            toolBox.deviceOffLineMassage(StudentFeeReceipt(), context)
            return
        }else{
            toolBox.startLoadingBar("Loading...", false, context)
            apiService.getStudentResponse(schoolId, studentId).enqueue(object : Callback<List<StudentFeeDetails>> {
                override fun onResponse(call: Call<List<StudentFeeDetails>>, response: Response<List<StudentFeeDetails>>) {
                    if (response.isSuccessful) {
                        toolBox.stopLoadingBar()
                        _studentFeesLiveData.postValue(response.body())
                    } else {
                        toolBox.showConfirmationDialog(
                            context = context,
                            title = "Fees Not Found",
                            positiveButtonText = "ok",
                            positiveButtonAction = { toolBox.activityChanger(MainActivity(), context) },
                            negativeButtonText = "",
                            negativeButtonAction = { },
                            cancelable = false)
                    }
                }

                override fun onFailure(call: Call<List<StudentFeeDetails>>, t: Throwable) {
                    toolBox.stopLoadingBar()
                    toolBox.showConfirmationDialog(
                        context = context,
                        title = "Warning !",
                        message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                        positiveButtonText = "OK",
                        positiveButtonAction = { toolBox.activityChanger(MainActivity(), context) },
                        negativeButtonText = "",
                        negativeButtonAction = { },
                        cancelable = false)
                }
            })
        }
    }

}