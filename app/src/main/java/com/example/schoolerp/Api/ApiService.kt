package com.example.schoolerp.Api

import com.example.schoolerp.DataClasses.AddExpenseData
import com.example.schoolerp.DataClasses.AddIncomeData
import com.example.schoolerp.DataClasses.AddaddPaySalaryResponse
import com.example.schoolerp.DataClasses.ClassResponseData
import com.example.schoolerp.DataClasses.EmployeeResponse
import com.example.schoolerp.DataClasses.InstituteProfileDataClass
import com.example.schoolerp.DataClasses.updateStudentPasswordResponse
import com.example.schoolerp.models.responses.AddCalander
import com.example.schoolerp.models.responses.AddFeeParticularResponse
import com.example.schoolerp.models.responses.AddMarkGradingResponse
import com.example.schoolerp.models.responses.AddTimeTableResponse
import com.example.schoolerp.models.responses.AddaccountchartResponse
import com.example.schoolerp.models.responses.AllClassNameResponse
import com.example.schoolerp.models.responses.ApiResponse
import com.example.schoolerp.models.responses.BaseOnInputMessageResponse
import com.example.schoolerp.models.responses.ClassAndStudentGetTuitionResponse
import com.example.schoolerp.models.responses.ClassDataResponse
import com.example.schoolerp.models.responses.ClassWiseSubjectResponse
import com.example.schoolerp.models.responses.ClassWithResposne
import com.example.schoolerp.models.responses.DeleteHomeworkResponse
import com.example.schoolerp.models.responses.DeleteResponse
import com.example.schoolerp.models.responses.DeleteSubjectResponse
import com.example.schoolerp.models.responses.EditEmpResponse
import com.example.schoolerp.models.responses.EmpAttendanceResponse
import com.example.schoolerp.models.responses.FeeParticularResponse
import com.example.schoolerp.models.responses.GetAccountSettingResponse
import com.example.schoolerp.models.responses.GetAcountChartResponse
import com.example.schoolerp.models.responses.GetCalanderResponse
import com.example.schoolerp.models.responses.GetEmployeesAttandadanceResponse
import com.example.schoolerp.models.responses.GetHomeworkResponse
import com.example.schoolerp.models.responses.GetMessageResponse
import com.example.schoolerp.models.responses.GetOverViewResponse
import com.example.schoolerp.models.responses.GetSalarySlipResponse
import com.example.schoolerp.models.responses.GetStudentAttandadanceDateWiseResponse
import com.example.schoolerp.models.responses.GetStudentAttandadanceResponse
import com.example.schoolerp.models.responses.GetTeacherPrincipleResponse
import com.example.schoolerp.models.responses.GetTimeTableResponse
import com.example.schoolerp.models.responses.GetTotalProfitResponse
import com.example.schoolerp.models.responses.GetgetCollectionStatusResponse
import com.example.schoolerp.models.responses.GetgetRevenueResponse
import com.example.schoolerp.models.responses.HomeWorkResponse
import com.example.schoolerp.models.responses.InstituteProfileResponse
import com.example.schoolerp.models.responses.LoginResponse
import com.example.schoolerp.models.responses.LogoutResponse
import com.example.schoolerp.models.responses.PromoteStudentRequest
import com.example.schoolerp.models.responses.PutHomeWorkResponse
import com.example.schoolerp.models.responses.PutTimeTableResponse
import com.example.schoolerp.models.responses.SearchHomeWorkHistoryResponse
import com.example.schoolerp.models.responses.SendMessageResponse
import com.example.schoolerp.models.responses.StudentAttendanceReportResponse
import com.example.schoolerp.models.responses.StudentAttendanceResponse
import com.example.schoolerp.models.responses.StudentDataResponse
import com.example.schoolerp.models.responses.StudentEditResponse
import com.example.schoolerp.models.responses.StudentFeeDetails
import com.example.schoolerp.models.responses.StudentPresentlyModel
import com.example.schoolerp.models.responses.SubjectDataResponse
import com.example.schoolerp.models.responses.TeacherNameResponse
import com.example.schoolerp.models.responses.UpatedFeesCollectionResponse
import com.example.schoolerp.models.responses.classUpdateResponse
import com.example.schoolerp.models.responses.getEmployeeCheckStatusResponse
import com.example.schoolerp.models.responses.getStudentCheckStatusResposne
import com.example.schoolerp.models.responses.getStudent_Idcard_Response
import com.example.schoolerp.models.responses.passwordResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
    @FormUrlEncoded
    @POST("/api/Logincontroller/login")
    fun login(
        @Field("number") schoolId: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/EmployeeController/addEmployee")
    fun addEmployee(@FieldMap fields: Map<String, String?>): Call<ApiResponse>

    @FormUrlEncoded
    @POST("api/SubjectController/addSubject")
    fun addSubject(@FieldMap fields: Map<String, String?>): Call<ApiResponse>

    @GET("api/EmployeeController/getEmployee/{schoolId}")
    fun getEmployees(@Path("schoolId") schoolId: String): Call<EmployeeResponse>
    // fun getEmployees(@QueryMap getEmployees: Map<String, String>): Call<EmployeeResponse>

    @GET("index.php/api/StudentController/get_classes/{school_id}")
    fun getClasses(@Path("school_id") schoolId: String): Call<ClassDataResponse>

    @FormUrlEncoded
    @POST("api/StudentController/addStudent")
    fun addStudent(@FieldMap studentData: Map<String, String>): Call<ApiResponse>

    @FormUrlEncoded
    @POST("api/AccountsController/income")
    fun addIncome(@FieldMap incomeData: Map<String, String>): Call<AddIncomeData>

    @FormUrlEncoded
    @POST("api/AccountsController/expense")
    fun addExpense(@FieldMap expenseData: Map<String, String>): Call<AddExpenseData>

    @GET("index.php/api/StudentController/get_student/{school_id}")
    fun getStudent(
        @Path("school_id") schoolId: String,
        @QueryMap getstudent: Map<String, String>
    ): Call<StudentDataResponse>

    @POST("index.php/api/Logincontroller/logout") fun logout(): Call<LogoutResponse>

    @FormUrlEncoded
    @PUT("index.php/api/StudentController/institute")
    suspend fun submitInstitute(
        @FieldMap fields: Map<String, String?>
    ): Response<InstituteProfileDataClass>

    @DELETE("index.php/api/StudentController/delete_student/{school_id}/{id}")
    fun deleteStudent(
        @Path("school_id") schoolId: String,
        @Path("id") studentId: String
    ): Call<Void>

    //    ------------------API for Detele Class Starting (hemant)-------------------
    @DELETE("index.php/api/SubjectController/deleteClass/{school_id}/{id}")
    fun deleteClass(@Path("school_id") schoolId: String, @Path("id") studentId: String): Call<Void>

    @DELETE("index.php/api/EmployeeController/delete_employee/{school_id}/{id}")
    fun deleteEmployee(
        @Path("school_id") schoolId: String,
        @Path("id") employeeId: String
    ): Call<DeleteResponse>

    @GET("index.php/api/StudentController/get_subject/{school_id}")
    fun getSubjeact(@Path("school_id") schoolId: String): Call<SubjectDataResponse>

    //    @GET("index.php/api/StudentController/get_subject/{school_id}")
    //    fun getSubjects(
    //        @Path("school_id") schoolId: String
    //    ): Call<SubjectResponse>

    //    ------------------API for Account Setting Starting (hemant)-------------------
    @GET("index.php/api/AccountsController/get_account/{school_id}")
    suspend fun getAccountSetting(
        @Path("school_id") schoolId: String
    ): Response<GetAccountSettingResponse>

    @GET("index.php/api/EmployeeController/get_role/{school_id}")
    fun getEmployeeData(@Path("school_id") schoolId: String): Call<TeacherNameResponse>

    @FormUrlEncoded
    @POST("index.php/api/AccountsController/update_account")
    fun updatePassword(@FieldMap fields: Map<String, String>): Call<passwordResponse>

    @GET("index.php/api/StudentController/get_student_attendence/{school_id}")
    fun getStudentAttandadance(
        @Path("school_id") schoolId: String
    ): Call<GetStudentAttandadanceResponse>

    @GET("index.php/api/StudentController/get_employee_attendence/{school_id}")
    fun getEmployeesAttandadance(
        @Path("school_id") schoolId: String
    ): Call<GetEmployeesAttandadanceResponse>

    @GET("index.php/api/StudentController/getAttendance/{school_id}/{class_name}")
    fun getStudentAttandadancedateWise(
        @Path("school_id") schoolId: String,
        @Path("class_name") class_name: String
    ): Call<GetStudentAttandadanceDateWiseResponse>

    //    ------------------API for Get class List Starting (hemant)-------------------
    @GET("api/SubjectController/get_Class/{schoolId}")
    fun getClassList(@Path("schoolId") schoolId: String): Call<AllClassNameResponse>
    //    ------------------API for Get class List Endind (hemant)-------------------

    //    ------------------API for update class Starting (hemant)-------------------
    @FormUrlEncoded
    @POST("index.php/api/SubjectController/updateClass/{schoolId}/{class_Id}")
    fun updateClass(
        @Path("schoolId") schoolId: String,
        @Path("class_Id") classId: String,
        @FieldMap updateClassfields: Map<String, String>
    ): Call<classUpdateResponse>
    //    ------------------API for update class Ending (hemant)-------------------

    //    ------------------API for Add class Starting (hemant)-------------------
    @FormUrlEncoded
    @POST("api/SubjectController/addClass")
    fun addClasss(@FieldMap classData: Map<String, String>): Call<ClassResponseData>
    //    ------------------API for Add class Starting (hemant)-------------------

    @FormUrlEncoded
    @POST("index.php/api/StudentController/classes_attendence/2024-11-15/KG-1/eraAbc")
    fun addStudentAttandance(
        @FieldMap StudentAttandanceData: Map<String, String>
    ): Call<StudentAttendanceResponse>

    @FormUrlEncoded
    @POST("index.php/api/StudentController/employee_attendence")
    fun addEmpAttandance(
        @FieldMap StudentAttandanceData: Map<String, String>
    ): Call<EmpAttendanceResponse>

    @FormUrlEncoded
    @PUT("index.php/api/EmployeeController/update_employee")
    fun EditEmployee(@FieldMap data: Map<String, String?>): Call<EditEmpResponse>

    @FormUrlEncoded
    @PUT("index.php/api/StudentController/update_student")
    fun EditStudent(@FieldMap EditStudentData: Map<String, String>): Call<StudentEditResponse>

    @GET("index.php/api/SubjectController/get_homework/{schoolId}/{employee_id}")
    fun getHomeWork(
        @Path("schoolId") schoolId: String,
        @Path("employee_id") employeeId: String,
    ): Call<GetHomeworkResponse>

    @FormUrlEncoded
    @POST("index.php/api/SubjectController/homework")
    fun addHomework(@FieldMap HomeWorkData: Map<String, String>): Call<HomeWorkResponse>

    @FormUrlEncoded
    @POST("index.php/api/SubjectController/subjects_by_class")
    fun addClassWiseSubject(
        @FieldMap ClassWiseSubjectData: Map<String, String>
    ): Call<ClassWiseSubjectResponse>

    // ------------------API for Update class with subject Starting (hemant)-------------------
    @FormUrlEncoded
    @PUT("index.php/api/StudentController/update_subject")
    fun updateSubject(@FieldMap subjectData: Map<String, String>): Call<ClassWithResposne>
    // ------------------API for Update class with subject Starting (hemant)-------------------

    @GET("index.php/api/EmployeeController/get_employees_by_role/{schoolId}")
    fun getTeacherPrinciple(@Path("schoolId") schoolId: String): Call<GetTeacherPrincipleResponse>

    @POST("index.php/api/SubjectController/search")
    @FormUrlEncoded
    fun searchHomeWorkHistory(
        @FieldMap params: Map<String, String>
    ): Call<SearchHomeWorkHistoryResponse>

    @POST("index.php/api/SubjectController/message")
    @FormUrlEncoded
    fun sendMessage(@FieldMap sendMessage: Map<String, String>): Call<SendMessageResponse>

    @POST("index.php/api/SubjectController/search_message")
    @FormUrlEncoded
    fun baseOnInputeMessage(
        @FieldMap baseOnInputMessage: Map<String, String>
    ): Call<BaseOnInputMessageResponse>

    @GET("index.php/api/SubjectController/get_message/{schoolId}/{employeeId}")
    fun getMessage(
        @Path("schoolId") schoolId: String,
        @Path("employeeId") employeeId: String,
    ): Call<GetMessageResponse>

    @POST("index.php/api/SubjectController/tution_fee")
    @FormUrlEncoded
    fun inputClassAndStudentGetTuitionData(
        @FieldMap classAndStudentGetTuitionData: Map<String, String>
    ): Call<ClassAndStudentGetTuitionResponse>

    @POST("index.php/api/SubjectController/addfeeparticular")
    @FormUrlEncoded
    fun AddFeeParticular(
        @FieldMap AddFeeParticularData: Map<String, String>
    ): Call<AddFeeParticularResponse>

    @DELETE("index.php/api/StudentController/delete_subject/{school_id}/{id}")
    fun deleteSubject(
        @Path("school_id") schoolId: String,
        @Path("id") subjectID: String
    ): Call<DeleteSubjectResponse>

    @GET("index.php/api/StudentController/get_student_idcard/{school_id}")
    fun getStudentIdcardDetails(
        @Path("school_id") schoolId: String
    ): Call<getStudent_Idcard_Response>

    @GET("index.php/api/Logincontroller/getUserInstituteDetails/{school_id}")
    fun getInstituteProfile(@Path("school_id") schoolId: String): Call<InstituteProfileResponse>

    @GET("index.php/api/StudentController/get_fee_particular/{school_id}/{st_name}")
    fun getFees(
        @Path("school_id") schoolId: String,
        @Path("st_name") class_name: String
    ): Call<FeeParticularResponse>

    @FormUrlEncoded
    @PUT("index.php/api/StudentController/update_feeparticular")
    fun UpateedFeesCollection(
        @FieldMap FeesCollectionData: Map<String, String>
    ): Call<UpatedFeesCollectionResponse>

    @DELETE("index.php/api/SubjectController/deleteHomework/{school_id}/{id}")
    fun deleteHomeWork(
        @Path("school_id") schoolId: String,
        @Path("id") homeworkId: String
    ): Call<DeleteHomeworkResponse>

    @FormUrlEncoded
    @PUT("index.php/api/SubjectController/update_homework")
    fun UpdatedHomework(@FieldMap HomeWorkData: Map<String, String>): Call<HomeWorkResponse>

    @GET("index.php/api/StudentController/get_attendance_summary/{school_id}/{student_id}")
    fun getAttendanceSummary(
        @Path("school_id") schoolId: String,
        @Path("student_id") studentId: String
    ): Call<StudentPresentlyModel>

    @GET("index.php/api/StudentController/get_employee_summary/{school_id}/{student_id}")
    fun getAttendanceEmpSummary(
        @Path("school_id") schoolId: String,
        @Path("student_id") studentId: String
    ): Call<StudentPresentlyModel>

    @POST("index.php/api/StudentController/timetable")
    @FormUrlEncoded
    fun addTimeTable(@FieldMap AddTimeTableData: Map<String, String>): Call<AddTimeTableResponse>

    @PUT("index.php/api/StudentController/update_timetable")
    @FormUrlEncoded
    fun PutTimeTable(@FieldMap PutTimeTableData: Map<String, String>): Call<PutTimeTableResponse>

    @GET("index.php/api/StudentController/get_timetable/{schoolId}")
    fun getTimeTable(@Path("schoolId") schoolId: String): Call<GetTimeTableResponse>

    @GET("index.php/api/AccountsController/get_revenue/{schoolId}")
    fun getRevenue(@Path("schoolId") schoolId: String): Call<GetgetRevenueResponse>

    @GET("index.php/api/AccountsController/get_profit/{schoolId}")
    fun getTotalProfit(@Path("schoolId") schoolId: String): Call<GetTotalProfitResponse>

    @GET("index.php/api/StudentController/get_calculate_fee_summary/{schoolId}")
    fun getCollectionStatus(
        @Path("schoolId") schoolId: String
    ): Call<GetgetCollectionStatusResponse>

    @POST("index.php/api/AccountsController/salary")
    @FormUrlEncoded
    fun addPaySalary(
        @FieldMap AddaddPaySalaryData: Map<String, String>
    ): Call<AddaddPaySalaryResponse>

    @GET("index.php/api/AccountsController/get_salary/{schoolId}")
    fun getSalarySlip(@Path("schoolId") schoolId: String): Call<GetSalarySlipResponse>

    @DELETE("index.php/api/StudentController/delete_timetable/{school_id}/{id}")
    fun deleteTimeTable(
        @Path("school_id") schoolId: String,
        @Path("id") employeeId: String
    ): Call<DeleteResponse>

    @FormUrlEncoded
    @PUT("index.php/api/StudentController/student_password")
    fun UpdateStudentPassword(
        @FieldMap StudentDelails: Map<String, String>
    ): Call<updateStudentPasswordResponse>

    @POST("index.php/api/AccountsController/account_chart")
    @FormUrlEncoded
    fun addaccountchart(
        @FieldMap AddaccountchartData: Map<String, String>
    ): Call<AddaccountchartResponse>

    @GET("index.php/api/AccountsController/get_account_chart/{schoolId}")
    fun getAcountChart(@Path("schoolId") schoolId: String): Call<GetAcountChartResponse>

    @DELETE("index.php/api/AccountsController/delete_acccount_chart/{school_id}/{id}")
    fun deleteChartAcount(
        @Path("school_id") schoolId: String,
        @Path("id") employeeId: String
    ): Call<DeleteResponse>

    @DELETE("index.php/api/SubjectController/delete_message/{school_id}/{id}")
    fun deleteMessage(
        @Path("school_id") schoolId: String,
        @Path("id") employeeId: String
    ): Call<DeleteResponse>

    @PUT("index.php/api/SubjectController/update_message")
    @FormUrlEncoded
    fun PutHomeWork(@FieldMap PutTimeTableData: Map<String, String>): Call<PutHomeWorkResponse>

    @GET("index.php/api/AccountsController/get_profites/{schoolId}")
    fun getOverView(@Path("schoolId") schoolId: String): Call<GetOverViewResponse>

    @POST("index.php/api/SubjectController/student_mark")
    @FormUrlEncoded
    fun addMarkGrading(
        @FieldMap AddMarkGradingData: Map<String, String>
    ): Call<AddMarkGradingResponse>

    @PUT("api/StudentController/update_promote_student")
    suspend fun promoteStudents(@Body request: PromoteStudentRequest): Response<ApiResponse>

    @GET("index.php/api/StudentController/get_fee/{school_id}/{student_id}")
    fun getStudentResponse(
        @Path("school_id") schoolId: String,
        @Path("student_id") employeeId: String
    ): Call<List<StudentFeeDetails>>

    /* @PUT("api/StudentController/update_promote_student")
        suspend fun promoteStudents(
            @Body request: PromoteStudentRequest
        ): Response<ApiResponse>
    */

    @FormUrlEncoded
    @POST("index.php/api/AccountsController/calender")
    fun addcalander(@FieldMap StudentDelails: Map<String, String>): Call<AddCalander>

    @GET("index.php/api/AccountsController/get_calender/{schoolId}")
    fun getcalender(@Path("schoolId") schoolId: String): Call<GetCalanderResponse>

    @GET("index.php/api/StudentController/get_student_monthattendence/{school_id}/{student_id}")
    fun getStudentAttendanceReport(
        @Path("school_id") schoolId: String,
        @Path("student_id") studentId: String
    ): Call<StudentAttendanceReportResponse>

    @GET("index.php/api/StudentController/get_student_status/{school_id}/{student_id}")
    fun getStudentCheckStatus(
        @Path("school_id") schoolId:String,
        @Path("student_id") studentId:String
    ) : Call<getStudentCheckStatusResposne>

    @GET("index.php/api/EmployeeController/get_employee_status/{school_id}/{employee_id}")
    fun getEmployeeCheckStatus(
        @Path("school_id") schoolId:String,
        @Path("employee_id") studentId:String
    ) : Call<getEmployeeCheckStatusResponse>

}
