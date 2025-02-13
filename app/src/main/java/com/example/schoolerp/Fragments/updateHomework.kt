package com.example.schoolerp.Fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.helpers.ImageConverter
import com.example.helpers.ImagePicker
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentDashBoardBinding
import com.example.schoolerp.databinding.FragmentUpdateHomeworkBinding
import com.example.schoolerp.repository.AddHomeworkRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AddHomeworkViewModel
import com.example.schoolerp.viewmodelfactory.AddHomeworkViewModelFactoryclass
import okhttp3.internal.platform.android.ConscryptSocketAdapter.Companion.factory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class updateHomework : Fragment() {
    private lateinit var binding: FragmentUpdateHomeworkBinding
    private lateinit var viewModelAddUpdateHomework: AddHomeworkViewModel
    private lateinit var sentBy:String
    private lateinit var date:String
    private lateinit var ClassId:String
    private lateinit var subject:String
    private lateinit var subjectID:String
    private lateinit var massage:String
    private lateinit var picture:String
    private lateinit var className:String
    private lateinit var SrId:String
    private var toolBox = MethodLibrary()
    private var currentPhotoPath: String? = null
    private lateinit var imageView: ImageView

    private val PERMISSION_REQUEST_CODE = 103

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200


    // var imagePicker = ImagePicker(requireContext(), imageView)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateHomeworkBinding.inflate(inflater, container, false)
        imageView = binding.Imgview

        viewModelInstial()
        getDataFromFragment()
        ClickEventActivate()
        return binding.root
    }

    private fun viewModelInstial(){
        val apiService = RetrofitHelper.getApiService()
        val repository = AddHomeworkRepository(apiService)
        val factory = AddHomeworkViewModelFactoryclass(repository)
        viewModelAddUpdateHomework = ViewModelProvider(this, factory).get(AddHomeworkViewModel::class.java)
    }

    private fun getDataFromFragment(){
        sentBy = arguments?.getString("sentBy").toString()
        date= arguments?.getString("date").toString()
        ClassId = arguments?.getString("ClassId").toString()
        subject = arguments?.getString("subject").toString()
        massage = arguments?.getString("massage").toString()
        picture = arguments?.getString("picture").toString()
        className = arguments?.getString("className").toString()
        subject = arguments?.getString("subject").toString()


        SrId = arguments?.getString("SrId").toString()

        binding.ClassName.setText("Class Name: $className")
        binding.etHomewoekMessageUpdate.setText("$massage")
        binding.subjectName.setText("Subject: $subject")

        val imageUrl = ImageUtil.getFullImageUrl("homework", picture)

        toolBox.displayImageSquare(imageUrl, binding.Imgview ,requireContext())
    }

    private fun  ClickEventActivate(){
        binding.btnAddUpdateHomework.setOnClickListener{

            val homeworkData = mapOf(
                "homework_detail" to binding.etHomewoekMessageUpdate.text.toString()+ " (Edited)",
                "classes" to ClassId,
                "homework_date" to date,
                "set_by" to sentBy,
                "school_id" to SchoolId().getSchoolId(requireContext()),
                "subject" to subject,
                "id" to SrId,
                "class_name" to className,
                "attachment" to ImageConverter().convertImageViewToBase64(binding.Imgview))

            viewModelAddUpdateHomework.updatedHomework(homeworkData as Map<String, String>)
            Log.d("UpdateHomework", homeworkData.toString())

            var result = viewModelAddUpdateHomework.updatedHomework(homeworkData as Map<String, String>)

            toolBox.showConfirmationDialog(
                context = requireContext(),
                title = "Homework Updated Successfully",
                //message = "Please select gallery or camera",
                positiveButtonText = "ok",
                positiveButtonAction = { toolBox.fragmentChanger(HomeWork(), requireContext()) },
                negativeButtonText = "",
                negativeButtonAction = {  },
                cancelable = true )

        }

        binding.Imgview.setOnClickListener{

           // imagePicker.showImagePickerDialog()

            toolBox.showConfirmationDialog(
                context = requireContext(),
                title = "Choose Upload Options",
                message = "Please select gallery or camera",
                positiveButtonText = "gallery",
                positiveButtonAction = { openGallery() },
                negativeButtonText = "camera",
                negativeButtonAction = { openCamera() },
                cancelable = true )
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        imagePicker.onActivityResult(requestCode, resultCode, data)
//    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a file to store the image
        val photoFile: File? = createImageFile()
        photoFile?.let {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.schoolerp.fileprovider",  // Use the correct authority
                it
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            if (cameraIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",  // suffix
            storageDir  // directory
        )
        currentPhotoPath = imageFile.absolutePath  // Save the file path to currentPhotoPath
        return imageFile
    }

    private fun rotateImage(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()

        // Rotate based on EXIF data
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_NORMAL -> {} // No rotation needed
        }

        // Ensure the image is not stretched or distorted
        var rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // If the image is landscape, check if it is rotated 90 or 270 degrees, and adjust
        if (bitmap.width > bitmap.height) {
            // Apply a rotation to make sure the image displays properly (portrait mode)
            rotatedBitmap = rotateToPortrait(rotatedBitmap)
        }

        return rotatedBitmap
    }

    private fun rotateToPortrait(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        // If the image is landscape (width > height), rotate it to portrait
        if (bitmap.width > bitmap.height) {
            matrix.postRotate(90f) // Rotate to portrait
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val photoFile = File(currentPhotoPath ?: return)
                    if (photoFile.exists()) {
                        // Decode the image file into a Bitmap
                        var bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                        // Correct the orientation of the image based on EXIF
                        val exif = ExifInterface(photoFile.absolutePath)
                        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                        // Rotate the bitmap based on EXIF orientation
                        bitmap = rotateImage(bitmap, orientation)

                        // Set the corrected image to the ImageView
                        imageView.setImageBitmap(bitmap)
                    }

                    // Clear the currentPhotoPath after using it
                    currentPhotoPath = null
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri: Uri = data?.data ?: return
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                        imageView.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



}