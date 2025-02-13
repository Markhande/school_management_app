package com.example.student

import android.content.Context
import android.content.SharedPreferences

class StudentDetails {

    private lateinit var sharedPreferences: SharedPreferences

    // Initialize SharedPreferences in the constructor
    fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
    }

    // Methods to retrieve student details from SharedPreferences
    fun getSchoolId(): String {
        return sharedPreferences.getString("school_id", "") ?: ""
    }

    fun getRole(): String {
        return sharedPreferences.getString("role", "") ?: ""
    }

    fun getStudentName(): String {
        return sharedPreferences.getString("st_name", "") ?: ""
    }

    fun getStudentClass(): String {
        return sharedPreferences.getString("st_class", "") ?: ""
    }

    fun getDateOfBirth(): String {
        return sharedPreferences.getString("dt_of_birth", "") ?: ""
    }

    fun getRegistrationNumber(): String {
        return sharedPreferences.getString("registration_no", "") ?: ""
    }

    fun getDateOfAdmission(): String {
        return sharedPreferences.getString("dt_of_admission", "") ?: ""
    }

    fun getDiscountFee(): String {
        return sharedPreferences.getString("discount_fee", "") ?: ""
    }

    fun getIdentificationMark(): String {
        return sharedPreferences.getString("identification_mark", "") ?: ""
    }

    fun getStudentBirthId(): String {
        return sharedPreferences.getString("st_birth_id", "") ?: ""
    }

    fun getPreviousSchool(): String {
        return sharedPreferences.getString("st_previous_school", "") ?: ""
    }

    fun getReligion(): String {
        return sharedPreferences.getString("religion", "") ?: ""
    }

    fun getBloodGroup(): String {
        return sharedPreferences.getString("blood_group", "") ?: ""
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean("onboarding_completed", false)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }


}
