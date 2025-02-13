package com.example.helpers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import android.widget.ImageView
import androidx.fragment.app.Fragment

class CaptureImage {

    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST_CODE = 2
    private lateinit var selectedImageUri: Uri
    private var imageView: ImageView? = null

    // Function to show the dialog for selecting the image source in a Fragment
    fun showImageSourceDialog(
        fragment: Fragment,
        imageViewId: ImageView  // Pass the ImageView directly
    ) {
        // Ensure the fragment is attached to an activity before continuing
        if (!fragment.isAdded) {
            return
        }

        imageView = imageViewId  // Set the ImageView

        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(fragment.requireContext()) // Safe use of context
        builder.setTitle("Select Image Source")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    // Option to take a photo
                    if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        openCamera(fragment)
                    } else {
                        fragment.requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                    }
                }
                1 -> {
                    // Option to choose from gallery
                    openGallery(fragment)
                }
                2 -> {
                    // Option to cancel
                }
            }
        }
        builder.show()
    }

    private fun openCamera(fragment: Fragment) {
        if (!fragment.isAdded) return // Check if fragment is still attached

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment.startActivityForResult(intent, CAMERA_REQUEST_CODE)  // Start activity from Fragment
    }

    private fun openGallery(fragment: Fragment) {
        if (!fragment.isAdded) return // Check if fragment is still attached

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        fragment.startActivityForResult(intent, PICK_IMAGE_REQUEST)  // Start activity from Fragment
    }

    // Handle the result from camera or gallery in the Fragment
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, fragment: Fragment) {
        if (!fragment.isAdded) return // Check if fragment is still attached

        if (resultCode == android.app.Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    // From Gallery
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        setImageToImageView(fragment)
                    }
                }
                CAMERA_REQUEST_CODE -> {
                    // From Camera
                    val photo: Bitmap = data?.extras?.get("data") as Bitmap
                    imageView?.setImageBitmap(photo)
                }
            }
        }
    }

    // Set the selected image URI to the ImageView
    private fun setImageToImageView(fragment: Fragment) {
        if (fragment.isAdded) {
            imageView?.setImageURI(selectedImageUri)
        }
    }
}
