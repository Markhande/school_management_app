package com.example.principle
import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.schoolerp.R

class PrincipalDetails {
    private lateinit var sharedPreferences: SharedPreferences

    fun getName(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("employee_name", null) ?: ""
    }

    fun getSchoolName(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("school_name", null) ?: ""
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

    fun getPrincipalNumber(context: Context): String {
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

    fun getPassword(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("password", null) ?: ""
    }


    fun getPrincipleImage(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("picture", null) ?: ""
    }
}
