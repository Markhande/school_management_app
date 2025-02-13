package com.example.helpers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


    class ImagePicker(private val context: Context, private val imageView: ImageView) {

        private val GALLERY_REQUEST_CODE = 1001
        private val CAMERA_REQUEST_CODE = 1002
        private var currentPhotoPath: String? = null

        fun showImagePickerDialog() {
            val options = arrayOf("Take Photo", "Choose from Gallery")
            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera() // Take photo using camera
                    1 -> openGallery() // Choose photo from gallery
                }
            }
            builder.show()
        }

        private fun openGallery() {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*"
            (context as? AppCompatActivity)?.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        private fun openCamera() {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile: File? = createImageFile()
            photoFile?.let {
                val photoURI: Uri = FileProvider.getUriForFile(
                    context,
                    "com.example.schoolerp.fileprovider",  // Use the correct authority
                    it
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                if (cameraIntent.resolveActivity(context.packageManager) != null) {
                    (context as? AppCompatActivity)?.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
                } else {
                    Toast.makeText(context, "Camera not available", Toast.LENGTH_SHORT).show()
                }
            }
        }

        @Throws(IOException::class)
        private fun createImageFile(): File? {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            val imageFile = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",  // suffix
                storageDir  // directory
            )
            currentPhotoPath = imageFile.absolutePath
            return imageFile
        }

        private fun rotateImage(bitmap: Bitmap, orientation: Int): Bitmap {
            val matrix = android.graphics.Matrix()

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                ExifInterface.ORIENTATION_NORMAL -> {}
            }

            var rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            // If the image is landscape, ensure it is rotated to portrait
            if (bitmap.width > bitmap.height) {
                rotatedBitmap = rotateToPortrait(rotatedBitmap)
            }

            return rotatedBitmap
        }

        private fun rotateToPortrait(bitmap: Bitmap): Bitmap {
            val matrix = android.graphics.Matrix()
            if (bitmap.width > bitmap.height) {
                matrix.postRotate(90f) // Rotate to portrait
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                when (requestCode) {
                    CAMERA_REQUEST_CODE -> {
                        val photoFile = File(currentPhotoPath ?: return)
                        if (photoFile.exists()) {
                            var bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                            // Correct the orientation of the image based on EXIF
                            val exif = ExifInterface(photoFile.absolutePath)
                            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                            bitmap = rotateImage(bitmap, orientation)

                            imageView.setImageBitmap(bitmap)
                        }
                        currentPhotoPath = null
                    }
                    GALLERY_REQUEST_CODE -> {
                        val selectedImageUri: Uri = data?.data ?: return
                        try {
                            val selectedImageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
                            imageView.setImageBitmap(selectedImageBitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
