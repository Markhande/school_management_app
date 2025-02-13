package com.example.schoolerp.Fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.ImageConverter
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.TimeTableData
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.adapter.TimeTableAdapter
import com.example.schoolerp.databinding.FragmentTimeTableAdminBinding
import com.example.schoolerp.repository.AddTimeTableRepository
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.GetTimeTableRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AddTimeTableViewModel
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.PutTimeTableViewModel
import com.example.schoolerp.viewmodel.TimeTableViewModel
import com.example.schoolerp.viewmodelfactory.AddTimeTableViewModelFactory
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetTimeTableViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeTableAdmin : Fragment() {
    private lateinit var binding:FragmentTimeTableAdminBinding
    private lateinit var viewModelSendTimeTable:AddTimeTableViewModel
    private lateinit var viewModelGetTimeTable: TimeTableViewModel
    private lateinit var adapter: TimeTableAdapter
    private var getTimeTableList: List<TimeTableData> = listOf()
    private lateinit var viewmodlePut:PutTimeTableViewModel
    val toolBox = MethodLibrary()
    private lateinit var imageView: ImageView


    var photo1: Bitmap? = null
    private val PERMISSION_REQUEST_CODE = 103
    private lateinit var viewModelAllClass: AllClassViewModel
    private var isExpand: Boolean = true
    private lateinit var mProgress: ProgressDialog
    private  var currentPhotoPath: String = null.toString()
    companion object {
        private val CAMERA_REQUEST_CODE = 100
        private val GALLERY_REQUEST_CODE = 200
    }
    private lateinit var connectivityReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTimeTableAdminBinding.bind(inflater.inflate(R.layout.fragment_time_table_admin,null))
        ModeViewSendTimeTable()
        AddTimeTable()
        setupAllClassObserver()
        setupViewModel()
        observal()
        initView()
        setupListners()
        registerConnectivityReceiver()
        mProgress = createProgressDialog()

        binding.layoutVisible.setOnClickListener{
            isExpand = !isExpand
            MethodLibrary().toggleLayoutExpansion(
                AnimationLayout = binding.layoutSendTimetable,
                PrimaryText = binding.sendTimetable,
                SecondText = binding.sendTimetable,
                startingHeight = 0,
                EndingHeight = 350,
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

        toolBox.clicked(binding.refreshImageLayout){
            ModeViewSendTimeTable()
            AddTimeTable()
            setupAllClassObserver()
            setupViewModel()
            observal()
            initView()
            setupListners()
            registerConnectivityReceiver()
            toolBox.rotateImage(
                imageView =  binding.refreshImage,
                duration = 1000
            )       }


        imageView = binding.imageStudent

        binding.imageStudent.setOnClickListener {
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



        return binding.root
    }
    private fun registerConnectivityReceiver() {
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let {
                    if (isInternetAvailable(it)) {
                        observal()
                    }
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(connectivityReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(connectivityReceiver)
    }
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun createProgressDialog(): ProgressDialog {
        return ProgressDialog(requireContext()).apply {
            setMessage("Please wait...")
            setCancelable(false)
        }
    }

    private fun setupListners(){

    }
    private fun toggleEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.txtForDefault.visibility = View.VISIBLE
            binding.GetTimeTableRecycler.visibility = View.GONE
        } else {
            binding.txtForDefault.visibility = View.GONE
            binding.GetTimeTableRecycler.visibility = View.VISIBLE
        }

      //  setuplistner(isEmpty)
    }


    private fun setupViewModel() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetTimeTableRepository(apiService)
        val factory = GetTimeTableViewModelFactory(repository)
        viewModelGetTimeTable = ViewModelProvider(this, factory)[TimeTableViewModel::class.java]
    }
private fun observal(){
    viewModelGetTimeTable.timeTableData.observe(viewLifecycleOwner) { result ->
        when {
            result.isSuccess -> {
                val data = result.getOrNull()?.data ?: emptyList()
                if (data.isNotEmpty()) {
                    adapter.updateData(data) // Update RecyclerView adapter
                    toggleEmptyView(false)  // Show RecyclerView
                } else {
                    toggleEmptyView(true)  // Show default text
                }
            }
            result.isFailure -> {
                toggleEmptyView(true) // Show default text on failure
               /* val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()*/
            }
        }
    }


    viewModelGetTimeTable.fetchTimeTable(SchoolId().getSchoolId(requireContext()))

        }
    private fun initView() {
        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            true
        )
        layoutManager.stackFromEnd = true
        binding.GetTimeTableRecycler.layoutManager = layoutManager

        adapter = TimeTableAdapter(
            getTimeTableList,
            object : TimeTableAdapter.TimeTableActionListener {
            override fun onEditClicked(data: TimeTableData) {
                var data = mapOf<String, String>(
                    "id" to data.id.toString(),
                    "picture" to data.picture.toString(),
                    "class_name" to data.classes.toString(),
                )
                toolBox.sendDataToFragment(requireContext(), EditTimeTable(), data)
//                val calssname = data.class_name?.trim()
//                val bundle = Bundle().apply {
//                    putParcelable("timetable", data)
//                    putString("class_name", calssname)
//                }
//
//                val EditTimeTable = EditTimeTable().apply {
//                    arguments = bundle
//                }
//
//                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, EditTimeTable)
//                    .addToBackStack(null)
//                    .commit()
            }


            override fun onDeleteClicked(data: TimeTableData) {
                toolBox.showConfirmationDialog(
                    context = requireContext(),
                    title = "Delete Confirmation",
                    message = "Are you sure you want to delete timetable for ${data.class_name}",
                    positiveButtonText = "yes",
                    positiveButtonAction = { deleteTimeTable(data) },
                    negativeButtonText = "no",
                    negativeButtonAction = {  },
                    cancelable = true )


//                AlertDialog.Builder(requireContext())
//                    .setTitle("Delete Confirmation")
//                    .setMessage("Are you sure you want to delete timetable for ${data.class_name}?")
//                    .setPositiveButton("Yes") { dialog, _ ->
//                        deleteTimeTable(data) // Implement this method to handle deletion
//                        dialog.dismiss()
//                    }
//                    .setNegativeButton("No") { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .create()
//                    .show()
            }
        })

        binding.GetTimeTableRecycler.adapter = adapter
    }

    private fun deleteTimeTable(data: TimeTableData) {
        val schoolId = data.school_id
        val employeeId = data.id

        viewModelGetTimeTable.deleteTimeTable(schoolId, employeeId)
        viewModelGetTimeTable.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show()

                val position = adapter.getTimeTableList().indexOf(data)
                if (position != -1) {
                    val updatedList = adapter.getTimeTableList().toMutableList()
                    updatedList.removeAt(position)

                    adapter.updateData(updatedList)
                    adapter.notifyItemRemoved(position)
                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ModeViewSendTimeTable() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AddTimeTableRepository(apiService)
        val factory = AddTimeTableViewModelFactory(repository)
        viewModelSendTimeTable = ViewModelProvider(this, factory).get(AddTimeTableViewModel::class.java)
    }
    private fun setupAllClassObserver() {
        val factory = AllClassViewModelFactory(AllClassRepository())
        viewModelAllClass = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)
        viewModelAllClass.getClasses(SchoolId().getSchoolId(requireContext()))
            .observe(viewLifecycleOwner) { response ->
                response?.data?.let { classList ->
                    if (classList.isNotEmpty()) {
                        val classNames =
                            mutableListOf("Select Class") // Add "Select Class" as the default item
                        val classIds =
                            mutableListOf("Select Class ID") // Add "Select Class ID" as the default item

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
        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
            return // Stop further execution if the device is offline
        }
        binding.textAttach.setOnClickListener {
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

        val schoolId = SchoolId().getSchoolId(requireContext())

        binding.btnAddMessage.setOnClickListener {
            if (validateEmployeeData()) {

                mProgress.show()

                val attachmentBase64 = photo1?.let { ImageUtil.bitmapToBase64(it) } ?: ""
                Log.d("AddMessage", "Attachment Base64: $attachmentBase64")
                val classId = binding.onlyId.selectedItem.toString()
                val sendMessage = mapOf(
                    "classes" to classId,
                    "picture" to ImageConverter().convertImageViewToBase64(binding.imageStudent),
                    "school_id" to schoolId
                )
                resetAttachment()
                observal()
                viewModelSendTimeTable.addTimeTable(sendMessage)
                    .observe(viewLifecycleOwner, { response ->
                        mProgress.dismiss()
                        if (response.isSuccess) {
                            showAddFeeParticularDialog()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to add timetable",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }
    private fun validateEmployeeData(): Boolean {
        mProgress.dismiss()

        val Specific = binding.AvailableData.selectedItemPosition.toString().trim()
        // val classemp = binding.AvailableData.selectedItemPosition.toString().trim()
        val messgate = binding.edtwriteMessage.text.toString().trim()

        return when {
            Specific.isEmpty() || Specific == "0" -> {
                showToast("Select a valid Specific Class")
                false
            }

            else -> true
        }
    }


    private fun showError(view: EditText, message: String) {
        view.error = message
        view.requestFocus()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun showAddFeeParticularDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Timetable added Successfully.",
            message = "Would you like to add more timetable?",
            positiveButtonText = "Yes",
            positiveButtonAction = { toolBox.fragmentChanger(TimeTableAdmin(), requireContext()) },
            negativeButtonText = "No",
            negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
    }

    private fun resetAttachment() {
        binding.textAttach.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green)) // Change to a default color (e.g., gray)
        binding.textAttach.text = "Attach Image"
        binding.textAttach.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        photo1 = null
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

