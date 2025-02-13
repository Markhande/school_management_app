package com.example.schoolerp.util

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

    var BASE_URL_IMAGE: String = "${RetrofitHelper.BASE_URL}assetsNew/img/"

    fun getFullImageUrl(subPath: String, imagePath: String): String {
        return "$BASE_URL_IMAGE$subPath/$imagePath"
    }

    fun bitmapToBase64(bitmap: Bitmap?, maxFileSizeMB: Int = 5): String? {
        if (bitmap == null) {
            return null
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        var quality = 100
        val maxFileSizeBytes = maxFileSizeMB * 1024 * 1024

        do {
            byteArrayOutputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            quality -= 10
        } while (byteArrayOutputStream.size() > maxFileSizeBytes && quality > 10)

        // Encode byte array to Base64 string
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }

    fun getBitmapFromImage(view: ImageView): Bitmap? {
        val drawable = view.drawable
        return if (drawable is BitmapDrawable) drawable.bitmap else null
    }

    fun encodeToBase64(filePath: String?, maxFileSizeMB: Int = 1): String? {
        try {
            val file = File(filePath ?: return null)
            val maxFileSizeBytes = maxFileSizeMB * 1024 * 1024

            if (file.length() > maxFileSizeBytes) {
                throw IOException("File size exceeds $maxFileSizeMB MB")
            }

            val fileInputStream = FileInputStream(file)
            val audioBytes = ByteArray(fileInputStream.available())
            fileInputStream.read(audioBytes)
            fileInputStream.close()

            return Base64.encodeToString(audioBytes, Base64.DEFAULT)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun getIndex(spinner: Spinner, myString: String?): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    fun getBase64StringFromImageView(imageView: ImageView, maxFileSizeMB: Int = 1): String? {
        val bitmap = getBitmapFromImage(imageView) ?: return null
        return bitmapToBase64(bitmap, maxFileSizeMB)
        fun getBase64StringFromImageView(imageView: ImageView): String {
            // Step 1: Get the Bitmap from the ImageView
            val bitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap

            // Step 2: Convert Bitmap to ByteArray
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Step 3: Encode ByteArray to Base64 String
            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

            return base64String
        }

        fun isImageSet(imageView: ImageView): Boolean {
            return imageView.drawable != null
        }

        fun base64ToBitmap(base64String: String?): Bitmap? {
            return try {
                val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                null
            }
        }
    }
}
