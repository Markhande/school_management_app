package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolerp.models.responses.GetStudentAttandadanceDateWiseResponse
import com.example.schoolerp.repository.DateWiseStudentAttendanceRepository
import kotlinx.coroutines.launch

class DateWiseStudentAttendanceViewModel(
    private val repository: DateWiseStudentAttendanceRepository
) : ViewModel() {

    val attendanceData: LiveData<GetStudentAttandadanceDateWiseResponse> get() = repository.attendanceData
    val error: LiveData<String> get() = repository.error

    fun fetchAttendance(schoolId: String,class_name:String) {
        viewModelScope.launch {
            repository.fetchAttendanceDateWise(schoolId,class_name)
        }
    }

}
