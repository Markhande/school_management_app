package com.example.schoolerp.onbarding

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.helpers.MethodLibrary
import com.example.onboardingschool.LoginPage
import com.example.onboardingschool.OnboardingItem
import com.example.onboardingschool.OnboardingItemsAdapter
import com.example.schoolerp.R

class Onboarding : AppCompatActivity() {

    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout
    private lateinit var btnGetStarted: Button
    private lateinit var sharedPreferences: SharedPreferences
    var toolBox = MethodLibrary()
    // The index of your third slide in the ViewPager


    @SuppressLint("ResourceAsColor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("onboarding_completed", false)) {
            Toast.makeText(this, sharedPreferences.getBoolean("onboarding_completed", false).toString(), Toast.LENGTH_SHORT).show()
            navigateToLoginPage()
            return
        }

        btnGetStarted = findViewById(R.id.btnGetStarted)
        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)

    }


    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.raw.onboarding1,
                    title = getString(R.string.onboarding_title1),
                    description = getString(R.string.onboarding_description1)

                ),
                OnboardingItem(
                    onboardingImage = R.raw.onboarding2,
                    title = getString(R.string.onboarding_title2),
                    description = getString(R.string.onboarding_description2)
                ),
                OnboardingItem(
                    onboardingImage = R.raw.onboarding3,
                    title = getString(R.string.onboarding_title3),
                    description = getString(R.string.oboarding_description3)
                )

            )
        )

        val onbordingViewPager = findViewById<ViewPager2>(R.id.onbordingViewPager)
        onbordingViewPager.adapter = onboardingItemsAdapter



        onbordingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {


                Log.e("TAG", "onPageSelected -->: $position")
                super.onPageSelected(position)
                if (position >= 2) btnGetStarted.visibility == View.VISIBLE



                setCurrentIndicator(position)
                if (position == onboardingItemsAdapter.itemCount - 1) {
                    btnGetStarted.setVisibility(View.VISIBLE);

                } else {
                    btnGetStarted.setVisibility(View.GONE);
                }
            }
        })




        (onbordingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
        findViewById<ImageView>(R.id.imgNext).setOnClickListener {
            if (onbordingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                onbordingViewPager.currentItem += 1
            } else {
                navigateToLoginPage()
            }
        }

        findViewById<TextView>(R.id.txtSkip).setOnClickListener {
//            sharedPreferences.edit().putBoolean("onboarding_completed", true).apply()
            navigateToLoginPage()
        }

        btnGetStarted.setOnClickListener {
//            sharedPreferences.edit().putBoolean("onboarding_completed", true).apply()
            navigateToLoginPage()
        }
    }


    private fun navigateToLoginPage() {
        startActivity(Intent(applicationContext, LoginPage::class.java))
        finish()
    }
    private fun setupIndicators() {
        indicatorsContainer = findViewById(R.id.indicatorsContainer)
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 8, 8, 8)

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.indicator_inactive_background
                    )
                )

                it.layoutParams = layoutParams
                indicatorsContainer.addView(it)
            }

        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.indicator_inactive_background
                    )
                )
            }


        }


    }
}