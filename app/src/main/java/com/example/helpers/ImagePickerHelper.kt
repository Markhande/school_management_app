package com.example.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ImagePickerHelper(private val context: Context, private val imageView: ImageView) {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectedImageUri: Uri

    // Initialize the launcher when calling openImageChooser
    fun openImageChooser() {
        // Initialize the launcher only when this method is called
        imagePickerLauncher = (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data ?: return@registerForActivityResult
                imageView.setImageURI(selectedImageUri) // Set the image in the ImageView
            }
        }

        // Open gallery to pick an image
        val intent = Intent().apply {
            type = "image/*"  // Filter for images
            action = Intent.ACTION_GET_CONTENT  // Open gallery
        }
        imagePickerLauncher.launch(intent)  // Launch the image picker
    }
}

