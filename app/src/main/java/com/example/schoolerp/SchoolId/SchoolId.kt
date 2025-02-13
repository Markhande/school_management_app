package com.example.schoolerp.SchoolId

import android.content.Context
import android.content.SharedPreferences
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.models.responses.AllClassNameResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SchoolId {

    private lateinit var sharedPreferences: SharedPreferences

    fun getSchoolId(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)

        return sharedPreferences.getString("school_id", null)?:""
    }


    fun getSchoolName(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("school_name", null)?: ""
    }

    fun getSchoolNumber(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("number", null)?: ""
    }

    fun getSchoolAdress(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("institute_address", null)?: ""
    }

    fun getSchoolWebSite(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("website", null)?: ""
    }

    fun getSchoolTagLine(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("tagline", null)?: ""
    }

    fun getSchoolEmail(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("email", null)?: ""
    }

    fun getLoginRole(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("role", null)?: ""
    }

    fun getUsername(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("username", null)?: ""
    }

    fun getPassword(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("password", null)?: ""
    }

    fun getLoginImage(context: Context): String {
        sharedPreferences = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return  sharedPreferences.getString("instituteLogoProfile", null)?: ""
    }

}