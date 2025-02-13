package com.example.schoolerp.util

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar

object LoadingUtil {

    // This method toggles the visibility of the ImageView to indicate loading
    fun toggleLoadingImageView(imageView: ImageView, isVisible: Boolean) {
        imageView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}

    // Method to hide the ProgressDialog
    // Method to toggle the visibility of a ProgressBar and set a message (for in-layout progress bars)

