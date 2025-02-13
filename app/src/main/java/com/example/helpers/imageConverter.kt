package com.example.helpers

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.ImageView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException

class ImageConverter {

    /**
     * Convert ImageView to a Base64 string (optimized image size).
     */
    fun convertImageViewToBase64(imageView: ImageView, maxWidth: Int = 1024, maxHeight: Int = 1024): String {
        // Get Bitmap from ImageView
        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap

        // If the Bitmap is null, return an empty string
        if (bitmap == null) {
            return ""
        }

        // Resize the Bitmap
        val resizedBitmap = resizeBitmap(bitmap, maxWidth, maxHeight)

        // Compress the Bitmap to JPEG (or any format you prefer)
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)  // 80% quality
        val byteArray = byteArrayOutputStream.toByteArray()

        // Convert the byte array to Base64 string
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /**
     * Resize the Bitmap to fit within maxWidth and maxHeight while maintaining the aspect ratio.
     */
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Calculate scaling factor
        val scaleFactor = Math.min(maxWidth.toFloat() / width, maxHeight.toFloat() / height)

        // Calculate the new dimensions
        val newWidth = (width * scaleFactor).toInt()
        val newHeight = (height * scaleFactor).toInt()

        // Create the resized bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Send the Base64 image to an API using OkHttp.
     */
    fun sendImageToApi(base64Image: String, callback: (Boolean, String?) -> Unit) {
        // Define the API endpoint URL
        val url = "https://your-api-endpoint.com/upload_image"

        // Create the JSON body for the API request
        val jsonBody = """
            {
                "image": "$base64Image"
            }
        """.trimIndent()

        // Create OkHttp client and request body
        val client = OkHttpClient()
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonBody)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        // Send the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
                callback(false, "Network Error: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Handle the successful response
                    callback(true, "Image uploaded successfully: ${response.body?.string()}")
                } else {
                    // Handle failure in API response
                    callback(false, "API Error: ${response.message}")
                }
            }
        })
    }
}
