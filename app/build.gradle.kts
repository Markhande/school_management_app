import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.schoolerp"
    compileSdk = 35
        defaultConfig {
            applicationId = "com.example.schoolerp"
            minSdk = 24
            targetSdk = 35
            versionCode = 1
            versionName = "1.0"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            release {

                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
        buildFeatures {
            viewBinding = true
            dataBinding = true
        }
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.lifecycle.livedata.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)
        implementation(libs.androidx.legacy.support.v4)
        implementation(libs.androidx.fragment.ktx)
        implementation(libs.androidx.activity)
      //  implementation(libs.androidx.recyclerview)
      //  implementation(libs.filament.android)

        implementation (libs.androidx.core.ktx.v1120)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        implementation(libs.mpandroidchart)
        implementation(libs.android.gif.drawable)
       implementation(libs.lottie)
        implementation(libs.converter.gson)
        implementation(libs.retrofit)
        implementation(libs.logging.interceptor)
        implementation(libs.androidx.lifecycle.viewmodel.ktx.v250)
        implementation(libs.androidx.lifecycle.livedata.ktx.v250)
        implementation (libs.glide)
       // implementation (libs.androidx.recyclerview.v130)
       // implementation("io.getstream:photoview:1.0.2")
//        implementation ("com.github.chrisbanes:PhotoView:latest.release.here")
//        implementation (libs.anychart.android)
//        implementation(libs.multidex)
      // implementation (libs.androidx.swiperefreshlayout)

        //noinspection GradlePluginVersion
/*
        implementation ("com.android.tools.build:gradle:2.1.2")
        implementation (libs.android.decoview.charting)
*/

       // implementation (libs.material.calendarview)


        implementation (libs.material.calendar.view)

    }
