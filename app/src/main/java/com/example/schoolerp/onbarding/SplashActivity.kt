// SplashActivity.kt
package com.example.schoolerp.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.helpers.MethodLibrary
import com.example.onboardingschool.LoginPage
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.databinding.ActivitySplashBinding
import com.example.schoolerp.onbarding.Onboarding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences

    val toolBox = MethodLibrary()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if ( toolBox.getDataString("Hello",this).equals("Hello")){
            toolBox.activityChanger(LoginPage(), this)
        }else{
            sharedPreferences = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)

            Handler(Looper.getMainLooper()).postDelayed({
                when {
                    !sharedPreferences.getBoolean("onboarding_completed", false) -> {
                        navigateTo(Onboarding::class.java)
                    }
                    !sharedPreferences.getBoolean("is_logged_in", false) -> {
                        navigateTo(LoginPage::class.java)
                    }
                    else -> {
                        navigateTo(MainActivity::class.java)
                    }
                }
            }, 1500)
        }

    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }
}

