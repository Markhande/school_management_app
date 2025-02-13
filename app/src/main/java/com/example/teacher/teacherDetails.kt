package com.example.teacher

import android.content.Context
import android.content.SharedPreferences

class TeacherDetails {
    private lateinit var sharedPreferences: SharedPreferences

    fun getName(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("employee_name", null) ?: ""
    }

    fun getSchoolName(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("school_name", null) ?: ""
    }

    fun getEmployeeID(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("employee_id", null) ?: ""
    }

    fun getSchoolLogo(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("institute_logo", null) ?: ""
    }

    fun getFatherName(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("f_h_name", null) ?: ""
    }

    fun getRole(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("role", null) ?: ""
    }

    fun getJoiningDate(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("date_of_joining", null) ?: ""
    }

    fun getStatus(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("status", null) ?: ""
    }

    fun getTeacherNumber(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("number", null) ?: ""
    }

    fun getNationalId(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("national_id", null) ?: ""
    }

    fun getEducation(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("education", null) ?: ""
    }

    fun getGender(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("gender", null) ?: ""
    }

    fun geReligion(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("religion", null) ?: ""
    }

    fun geBloodGroup(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("blood_group", null) ?: ""
    }

    fun getHomeAddress(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("home_address", null) ?: ""
    }

    fun getEmail(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", null) ?: ""
    }

    fun getUsername(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", null) ?: ""
    }

    fun getProfile(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("picture", null) ?: ""
    }

    fun getTeacherId(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("employee_id", null) ?: ""
    }
}