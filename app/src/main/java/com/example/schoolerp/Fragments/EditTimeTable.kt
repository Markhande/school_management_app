package com.example.schoolerp.Fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.TimeTableData
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentEditTimeTableBinding
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.PutTimeTableRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.PutTimeTableViewModel
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.PutTimeTableViewModelFactory
import com.example.student.FullScreenImageActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditTimeTable : Fragment() {
    private lateinit var binding: FragmentEditTimeTableBinding
    private lateinit var viewmodlePut:PutTimeTableViewModel
    var photo1: Bitmap? = null
    private val PERMISSION_REQUEST_CODE = 103
    private lateinit var viewModelAllClass: AllClassViewModel
    private var isExpand: Boolean = false
    private var toolbox = MethodLibrary()
    private lateinit var mProgress: ProgressDialog

    private  var className:String? = ""
    private  var id:String? = ""
    private  var picture:String? = ""
    private  var currentPhotoPath: String = null.toString()
    private lateinit var imageView: ImageView

    companion object {
        private val CAMERA_REQUEST_CODE = 100
        private val GALLERY_REQUEST_CODE = 200
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditTimeTableBinding.inflate(inflater, container, false)

//        arguments?.getParcelable<TimeTableData>("timetable")?.let { timeTableData ->
//            populateFields(timeTableData)
//        }

//        arguments?.getString("class_name")?.let { className ->
//            binding.txtClassName1.text = className
//            Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
//        }

        className =  arguments?.getString("class_name")
        picture = arguments?.getString("picture")
        id = arguments?.getString("id")

        val imageUrl = picture?.let { ImageUtil.getFullImageUrl("timetable", it) }

        if (imageUrl != null) {
            toolbox.displayImageSquare(imageUrl, binding.bigImageView, binding.root.context)
        }

        imageView = binding.bigImageView

        toolbox.showConfirmationDialog(
            context = requireContext(),
            title = "Choose Upload Options",
            message = "Please select gallery or camera",
            positiveButtonText = "gallery",
            positiveButtonAction = { openGallery() },
            negativeButtonText = "camera",
            negativeButtonAction = { openCamera() },
            cancelable = true )

        binding.imageLayout.setOnClickListener{
            toolbox.showConfirmationDialog(
                context = requireContext(),
                title = "Choose Upload Options",
                message = "Please select gallery or camera",
                positiveButtonText = "gallery",
                positiveButtonAction = { openGallery() },
                negativeButtonText = "camera",
                negativeButtonAction = { openCamera() },
                cancelable = true )
        }

        binding.updateTimetable.setOnClickListener {
            AddTimeTable()
        }

        ModeViewPutTimeTable()
        setupAllClassObserver()
        mProgress = createProgressDialog()

        /*  binding.layoutVisible.setOnClickListener {
              binding.layoutSendmessage.visibility =
                  if (binding.layoutSendmessage.visibility == View.GONE) {
                      View.VISIBLE
                  } else {
                      binding.edtwriteMessage.text.clear()
                      View.GONE
                  }
          }*/
        binding.layoutVisible.setOnClickListener{
            isExpand = !isExpand
            MethodLibrary().toggleLayoutExpansion(
                AnimationLayout = binding.layoutSendTimetable,
                PrimaryText = binding.sendTimetable,
                SecondText = binding.sendTimetable,
                startingHeight = 0,
                EndingHeight = 300,
                Timing = 500,
                defaultValue = isExpand,
                requireContext()
            )
        }

        binding.edtwriteMessage.setOnTouchListener { v, event ->
            if (v.id == R.id.edtwriteMessage) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }



        return binding.root
    }
    private fun createProgressDialog(): ProgressDialog {
        return ProgressDialog(requireContext()).apply {
            setMessage("Please wait...")
            setCancelable(false)
        }
    }

    private fun ModeViewPutTimeTable() {
        val apiService = RetrofitHelper.getApiService()
        val repository = PutTimeTableRepository(apiService)
        val factory = PutTimeTableViewModelFactory(repository)
        viewmodlePut = ViewModelProvider(this, factory).get(PutTimeTableViewModel::class.java)
    }
    private fun populateFields(timeTableData: TimeTableData) {
        binding.txtClassName1.text = timeTableData.id ?: "No class name"
        val imageUrl = ImageUtil.getFullImageUrl("timetable", timeTableData.picture)
        setSpinnerSelection(binding.AvailableData, timeTableData.class_name)
        Glide.with(binding.root.context)
            .load(imageUrl)
            .error(R.drawable.imagenotfound)
            .into(binding.bigImageView)

        binding.bigImageView.setOnClickListener {
            val context = binding.bigImageView.context
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                putExtra("image_url", imageUrl)
            }
            context.startActivity(intent)
        }


    }

    private fun setSpinnerSelection(spinner: Spinner, value: String?) {
        val adapter = spinner.adapter
        if (adapter != null && value != null) {
            for (i in 1 until adapter.count) { // Start from the second item
                val item = adapter.getItem(i)?.toString()?.trim()
                if (item.equals(value.trim(), ignoreCase = true)) {
                    spinner.setSelection(i)
                    break
                }
            }
        }
    }

    private fun setupAllClassObserver() {
        val factory = AllClassViewModelFactory(AllClassRepository())
        viewModelAllClass = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)
        viewModelAllClass.getClasses(SchoolId().getSchoolId(requireContext()))
            .observe(viewLifecycleOwner) { response ->
                response?.data?.let { classList ->
                    if (classList.isNotEmpty()) {
                        val classNames =
                            mutableListOf("Select Class")
                        val classIds =
                            mutableListOf("Select Class ID")

                        classNames.addAll(classList.map { it.class_name })
                        classIds.addAll(classList.map { it.class_id.toString() })

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            classNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        val adapter2 = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            classIds
                        )
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        binding.AvailableData.adapter = adapter
                        binding.onlyId.adapter = adapter2

                        binding.AvailableData.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parentView: AdapterView<*>,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedClassName =
                                        parentView.getItemAtPosition(position) as String

                                    val filteredClassIds =
                                        classList.filter { it.class_name == selectedClassName }
                                            .map { it.class_id.toString() }

                                    if (filteredClassIds.isNotEmpty()) {
                                        val updatedAdapter2 = ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_spinner_item,
                                            filteredClassIds
                                        )
                                        updatedAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        binding.onlyId.adapter = updatedAdapter2
                                    } else {
                                        binding.onlyId.adapter = ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_spinner_item,
                                            listOf("Select Class ID")
                                        )
                                    }
                                }

                                override fun onNothingSelected(parentView: AdapterView<*>) {
                                    binding.onlyId.adapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        listOf("Select Class ID")
                                    )
                                }
                            }
                    } else {
                        Toast.makeText(requireContext(), "No classes found", Toast.LENGTH_SHORT)
                            .show()
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT)
                        .show()
                }

            }
    }


    private fun AddTimeTable() {

//        binding.textAttach.setOnClickListener {
//          //  showImagePickerDialog(REQUEST_IMAGE_CAPTURE_1, REQUEST_IMAGE_GALLERY_1)
//        }

//        val schoolId = SchoolId().getSchoolId(requireContext())


            mProgress.show()
            //if (validateEmployeeData()) {
                mProgress.show()

                /* val attachmentBase64 = photo1?.let { ImageUtil.bitmapToBase64(it) } ?: ""
            Log.d("AddMessage", "Attachment Base64: $attachmentBase64")
          */
                val attachmentBase64 = photo1?.let {
                    ImageUtil.bitmapToBase64(it) // Convert Bitmap to Base64 if photo1 is available
                } ?: ImageUtil.getBase64StringFromImageView(binding.bigImageView)


                val sendMessage = mapOf(
                    "classes" to className,
                    "picture" to attachmentBase64,
                    "school_id" to SchoolId().getSchoolId(requireContext()),
                    "id" to id
                )

                Log.d("AddMessage", "Send Message Map: $sendMessage")

                viewmodlePut.putTimeTable(sendMessage as Map<String, String>)
                mProgress.dismiss()

//                Toast.makeText(context, "TimeTable updated successfully!", Toast.LENGTH_SHORT)
//                    .show()


                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Data Updated Successfully.")
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                        mProgress.dismiss()
                        toolbox.fragmentChanger(TimeTableAdmin() ,requireActivity())
                    }
                    .create()
                    .show()

    }

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
                    currentPhotoPath = null.toString()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}


