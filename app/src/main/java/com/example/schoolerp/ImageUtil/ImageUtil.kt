package com.example.schoolerp.ImageUtil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.ImageView
import android.widget.Spinner
import com.example.schoolerp.Api.RetrofitHelper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException


object ImageUtil {

    val baseUrl = RetrofitHelper.BASE_URL
    val BASE_URL_IMAGE: String = baseUrl

    fun bitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) {
            return null
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        // Compress the bitmap into a JPEG or PNG format (change format if needed)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Encode byte array to Base64 string
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getBitmapFromImage(view: ImageView): Bitmap? {
        // Get the drawable from the ImageView
        val drawable = view.drawable

        // Check if drawable is a BitmapDrawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            return bitmap
        }
        return null
    }

    fun encodeToBase64(filePath: String?): String? {
        try {
            // Read MP3 file
            val fileInputStream = FileInputStream(filePath)
            val audioBytes = ByteArray(fileInputStream.available())
            fileInputStream.read(audioBytes)
            fileInputStream.close()

            // Encode audio bytes to Base64
            val base64Encoded = Base64.encodeToString(audioBytes, Base64.DEFAULT)
            return base64Encoded
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    @Throws(IOException::class)
    private fun encodeAudioToBase64(filePath: String): String {
        val audioByteArray = convertFileToByteArray(filePath)
        return Base64.encodeToString(audioByteArray, Base64.DEFAULT)
    }

    @Throws(IOException::class)
    private fun convertFileToByteArray(filePath: String): ByteArray {
        val file = File(filePath)
        val fileInputStream = FileInputStream(file)
        val data = ByteArray(file.length().toInt())
        fileInputStream.read(data)
        fileInputStream.close()
        return data
    }

    // get Index of matching string from spinner
    fun getIndex(spinner: Spinner, myString: String?): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }

        return 0
    }

    fun getBase64StringFromImageView(imageView: ImageView): String {
        // Step 1: Get the Bitmap from the ImageView
        val bitmapDrawable = imageView.drawable as BitmapDrawable
        val bitmap = bitmapDrawable.bitmap

        // Step 2: Convert Bitmap to ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Step 3: Encode ByteArray to Base64 String
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        return base64String
    }

    fun isImageSet(imageView: ImageView): Boolean {
        val drawable = imageView.drawable
        return drawable != null
    }

    // Convert Base64 string to Bitmap
    fun base64ToBitmap(base64String: String?): Bitmap? {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return null // If there's an issue with decoding, return null
        }
    }
}