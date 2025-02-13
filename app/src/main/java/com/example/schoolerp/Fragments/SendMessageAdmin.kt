package com.example.schoolerp.Fragments

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.example.helpers.ImageConverter
import com.example.helpers.MethodLibrary
import com.example.helpers.validation
import com.example.schoolerp.Adapter.GetMessageAdapter
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.GetMessageData
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentSendMessageAdminBinding
import com.example.schoolerp.repository.AddMessageRepository
import com.example.schoolerp.repository.BaseOnInputeMessageRepository
import com.example.schoolerp.repository.GetMessageRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AddMessageViewModel
import com.example.schoolerp.viewmodel.BaseOnInputMessageViewModel
import com.example.schoolerp.viewmodel.GetMessageViewModel
import com.example.schoolerp.viewmodelfactory.AddMessageViewModelFactory
import com.example.schoolerp.viewmodelfactory.BaseOnInputeMessageViewModelFactory
import com.example.schoolerp.viewmodelfactory.GetMessageViewModelFactory
import com.example.student.StudentInfo
import com.example.teacher.TeacherDetails
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SendMessageAdmin : Fragment(), GetMessageAdapter.OnMessageActionListener {
    private lateinit var binding: FragmentSendMessageAdminBinding
    private lateinit var viewModelSendMessage: AddMessageViewModel
    private lateinit var viewModelInputeMessage: BaseOnInputMessageViewModel
    private lateinit var viewModelGetmessage: GetMessageViewModel
    private val CAMERA_REQUEST_CODE = 1001
    private val GALLERY_REQUEST_CODE = 1002
    private var imageUri: Uri? = null
    private lateinit var getMessageData: List<GetMessageData>
    private lateinit var getmessageadapter: GetMessageAdapter
    private var isExpand: Boolean = true
    var photo1: Bitmap? = null
    private val REQUEST_CODE_CAMERA = 101
    private val REQUEST_CODE_GALLERY = 102
    private val PERMISSION_REQUEST_CODE = 103
    val toolbox = validation()
    val toolBox=MethodLibrary()
    private lateinit var teacherNameRef:String
    private var isTeacherRole:Boolean = true

    private lateinit var imageView: ImageView
    private var currentPhotoPath: String? = null

    private lateinit var mProgress: ProgressDialog
    private lateinit var connectivityReceiver: BroadcastReceiver


    companion object {
        const val REQUEST_IMAGE_CAPTURE_1: Int = 1
        const val REQUEST_IMAGE_GALLERY_1: Int = 11
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSendMessageAdminBinding.bind(
            inflater.inflate(
                R.layout.fragment_send_message_admin,
                null
            )
        )

        ModeViewSendMessage()
        ModelViewBaseonInpute()
        AddMessage()
        setupSpinners()
        ModelViewGetMessage()
        initView()
        initobserval()
        getSchoolId()
        setUpLiners()
        mProgress=createProgressDialog()
        registerConnectivityReceiver()

        binding.writeMessage.setOnClickListener{
            isExpand = !isExpand
            MethodLibrary().toggleLayoutExpansion(
                AnimationLayout = binding.layoutSendmessage,
                PrimaryText = binding.writeMessage,
                SecondText = binding.writeMessage,
                startingHeight = 10,
                EndingHeight = 480,
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
            toolBox.rotateImage(
                imageView =  binding.refreshImage,
                duration = 1000
            )
            ModeViewSendMessage()
            ModelViewBaseonInpute()
            AddMessage()
            setupSpinners()
            ModelViewGetMessage()
            initView()
            initobserval()
            getSchoolId()
            setUpLiners()
            mProgress=createProgressDialog()
            registerConnectivityReceiver()
        }

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

        imageView = binding.imageStudent
        return binding.root
    }
    private fun registerConnectivityReceiver() {
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let {
                    if (isInternetAvailable(it)) {
                        initobserval()

                    }
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(connectivityReceiver, intentFilter)
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
            requireContext().unregisterReceiver(connectivityReceiver)
        }catch (e : RuntimeException){
            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
        }

    }
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    private fun setUpLiners(){
        initobserval()
//        binding.RefreshAll.setOnClickListener(){
//
//
//        }
    }
    private fun createProgressDialog(): ProgressDialog {
        return ProgressDialog(requireContext()).apply {
            setMessage("Please wait...")
            setCancelable(false)
        }
    }

    private fun getSchoolId(): String {
        val sharedPreferences = requireActivity().getSharedPreferences(
            "onboarding_prefs",
            AppCompatActivity.MODE_PRIVATE
        )
        val schoolId = sharedPreferences.getString("school_id", null)

        if (schoolId != null) {
            Log.d("AddNewEmployees", "School ID retrieved from SharedPreferences: $schoolId")
        } else {
            Log.d("AddNewEmployees", "School ID not found in SharedPreferences")
        }

        return schoolId ?: "defaultSchoolId"
    }

    private fun ModelViewGetMessage() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetMessageRepository(apiService)
        val factory = GetMessageViewModelFactory(repository)
        viewModelGetmessage = ViewModelProvider(this, factory).get(GetMessageViewModel::class.java)
    }

    private fun initView() {

        getMessageData = mutableListOf()
        // Set up the LayoutManager with reversed layout and stack from end
        val layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,true)
        layoutManager.stackFromEnd = true // Start from the end of the list

        binding.GetMessageRecycler.layoutManager = layoutManager
        // Initialize the adapter
        getmessageadapter = GetMessageAdapter(getMessageData as MutableList<GetMessageData>, this)
        binding.GetMessageRecycler.adapter = getmessageadapter

    }

    private fun initobserval() {
        var stname = StudentInfo().getStudentName(requireContext())
        var stClass = StudentInfo().getStudentClass(requireContext())
        viewModelGetmessage.fetchMessages(SchoolId().getSchoolId(requireContext()), TeacherDetails().getTeacherId(requireContext()))


        viewModelGetmessage.messages.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val data = response.data ?: emptyList()


                Log.d(
                    "GetMessageFragment",
                    "Fetched ${data.size} messages."
                ) // Log the number of messages

                if (data.isNotEmpty()) {
                    getmessageadapter.submitList(data) // Pass data to the adapter
                 //   Log.d("GetMessageFragment", "Messages passed to adapter.")
                    toggleEmptyView(false) // Data available, show the RecyclerView

                } else {
                  //  Log.d("GetMessageFragment", "No messages found.")
                    toggleEmptyView(true) // No data, show the default message

                }
            } else {
                // Handle null response
              //  val errorMessage = "Failed to fetch messages."
               // Log.e("GetMessageFragment", errorMessage) // Log the error
                // Optionally, show an error message to the user
                // showError(errorMessage)
                toggleEmptyView(true) // No data available, show the default message

            }
        }
    }
    private fun toggleEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            // Show the "No data" text and hide the RecyclerView
            binding.txtForDefault.visibility = View.VISIBLE
            binding.GetMessageRecycler.visibility = View.GONE
        } else {
            // Show the RecyclerView and hide the "No data" text
            binding.txtForDefault.visibility = View.GONE
            binding.GetMessageRecycler.visibility = View.VISIBLE
        }

        //setuplistner(isEmpty)
    }

    private fun ModelViewBaseonInpute() {
        val apiService = RetrofitHelper.getApiService()
        val repository = BaseOnInputeMessageRepository(apiService)
        val factory = BaseOnInputeMessageViewModelFactory(repository)
        viewModelInputeMessage =
            ViewModelProvider(this, factory).get(BaseOnInputMessageViewModel::class.java)
    }

    private fun setupSpinners() {
        val spinner1 = binding.spSendBy1 // Internal spinner
        val spinner2 = binding.spSendBy2 // Visible spinner for user
        val availableDataSpinner = binding.AvailableData // Spinner to display fetched data

        // Internal data for both spinners
        val spinner1Data = listOf(
            "SELECT AN OPTION",
            "Specific class",
            "Specific Student",
            "Specific Employee"
        ) // User-friendly display
        val spinner2Data = listOf(
            "select_an_option",
            "specific_class",
            "specific_student",
            "specific_employee"
        ) // Internal values

        // Set adapter for Spinner 1 (internal spinner)
        val adapter1 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            spinner1Data
        )
        spinner1.adapter = adapter1

        // Set adapter for Spinner 2 (visible to the user, showing user-friendly values)
        val adapter2 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            spinner1Data // Show spinner1Data values to the user
        )
        spinner2.adapter = adapter2

        // Set default selection for Spinner 1 and Spinner 2
        spinner1.setSelection(0)
        spinner2.setSelection(0)

        // Prevent infinite looping by tracking which spinner triggered the event
        var isSpinner1Triggered = false
        var isSpinner2Triggered = false

        // Listener for Spinner 1 (internal spinner)
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isSpinner1Triggered) {
                    isSpinner2Triggered = true // Mark spinner2 as being updated programmatically
                    spinner2.setSelection(position)
                    isSpinner2Triggered = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Listener for Spinner 2 (visible spinner)
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isSpinner2Triggered) {
                    isSpinner1Triggered = true // Mark spinner1 as being updated programmatically
                    spinner1.setSelection(position)
                    isSpinner1Triggered = false

                    // Handle the selected item using internal spinner2Data
                    val selectedItemInternal = spinner2Data[position]
                    handleSelection(selectedItemInternal, availableDataSpinner)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun synchronizeInternalSpinner(spinner1: Spinner, selectedItem: String) {
        val data = listOf(selectedItem) // Update internal spinner with the same data
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            data
        )
        spinner1.adapter = adapter

        // Automatically set the selected item in Spinner 1
        spinner1.setSelection(0)
    }

    private fun handleSelection(selectedItem: String, availableDataSpinner: Spinner) {
        fetchDataBasedOnSelection(selectedItem) { data ->
            Log.d("SpinnerSetup", "Fetched data for $selectedItem: $data")

            // If no data, display a message
            val dataToDisplay = if (data.isEmpty()) listOf("No data available") else data

            // Log the data to ensure it is correct
            Log.d("SpinnerSetup", "Data to display in spinner: $dataToDisplay")

            // Update the spinner adapter with data on the UI thread
            requireActivity().runOnUiThread {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    dataToDisplay
                )
                availableDataSpinner.adapter = adapter

                // Set spinner visibility based on the data
                availableDataSpinner.visibility = if (data.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun fetchDataBasedOnSelection(selectedItem: String, callback: (List<String>) -> Unit) {
        val schoolId = SchoolId().getSchoolId(requireContext())
        Log.d("SpinnerSetup", "School ID: $schoolId")

        val inputMap = mapOf(
            "classes" to selectedItem,
            "students" to selectedItem,
            "employees" to selectedItem,
            "school_id" to schoolId,
            "role" to SchoolId().getLoginRole(requireContext())
        )

        Log.d("SpinnerSetup", "Input Map: $inputMap")

        // Make the API call
        viewModelInputeMessage.getBaseOnInputMessage(inputMap)

        // Observe the API response
        viewModelInputeMessage.baseOnInputMessageResponse.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Log.d(
                    "SpinnerSetup",
                    "API response received: ${response.data}"
                )  // Log the entire response data

                if (response.status) {
                    Log.d("SpinnerSetup", "API response status is true, data: ${response.data}")

                    // Process the data based on the selected item
                    val dataToDisplay = when (selectedItem) {
                        "specific_class" -> {
                            val classesData = handleClassesData(response.data.classes)
                            Log.d("SpinnerSetup", "Classes Data: $classesData")
                            classesData
                        }

                        "specific_student" -> {
                            val studentsData = handleStudentsData(response.data.students)
                            Log.d("SpinnerSetup", "Students Data: $studentsData")
                            studentsData
                        }

                        "specific_employee" -> {
                            val employeesData = handleEmployeesData(response.data.employees)
                            Log.d("SpinnerSetup", "Employees Data: $employeesData")
                            employeesData
                        }

                        else -> {
                            Log.d("SpinnerSetup", "No matching selection for: $selectedItem")
                            emptyList()
                        }
                    }
                    callback(dataToDisplay)  // Callback with the processed data
                } else {
                    Log.e("SpinnerSetup", "API response status is false: ${response.Message}")
                    callback(emptyList())  // Callback with an empty list if the response is not successful
                }
            }
            result.onFailure { error ->
                Log.e("SpinnerSetup", "API call failed: ${error.message}")
                callback(emptyList())  // Callback with an empty list in case of failure
            }
        }
    }

    private fun handleClassesData(classesData: Any?): List<String> {
        return when (classesData) {
            is List<*> -> {
                // Check if it's a list of Maps
                if (classesData.isNotEmpty() && classesData[0] is Map<*, *>) {
                    // Map the list of maps to class names
                    classesData.mapNotNull { (it as? Map<*, *>)?.get("class_id") as? String }
                    classesData.mapNotNull { (it as? Map<*, *>)?.get("class_name") as? String }
                } else {
                    Log.d("SpinnerSetup", "Classes data is empty or malformed: $classesData")
                    emptyList()
                }
            }

            else -> {
                Log.d("SpinnerSetup", "Classes data is not a List: $classesData")
                emptyList()
            }
        }
    }

    private fun handleStudentsData(studentsData: Any?): List<String> {
        return if (studentsData is List<*>) {
            studentsData.mapNotNull { student ->
                (student as? Map<*, *>)?.let {
                    val id = it["student_id"] as? String
                    val name = it["student_name"] as? String
                    if (id != null && name != null) "$name" else null
                }
            }
        } else {
            emptyList()
        }
    }

    private fun handleEmployeesData(employeesData: Any?): List<String> {
        return if (employeesData is List<*>) {
            employeesData.mapNotNull { employee ->
                (employee as? Map<*, *>)?.let {
                    val id = it["employee_id"] as? String
                    val name = it["employee_name"] as? String
                    if (id != null && name != null) "$name" else null
                }
            }
        } else {
            emptyList()
        }
    }

    private fun ModeViewSendMessage() {
        val apiService = RetrofitHelper.getApiService()
        val repository = AddMessageRepository(apiService)
        val factory = AddMessageViewModelFactory(repository)
        viewModelSendMessage = ViewModelProvider(this, factory).get(AddMessageViewModel::class.java)
    }

    private fun AddMessage() {
        if (!toolBox.isInternetAvailable(requireContext())) {
            toolBox.deviceOffLineMassage(requireContext())
            return // Stop further execution if the device is offline
        }
        binding.imageStudent.setOnClickListener {
            showImagePickerDialog(REQUEST_IMAGE_CAPTURE_1, REQUEST_IMAGE_GALLERY_1)
        }

        val schoolId = SchoolId().getSchoolId(requireContext())

        binding.btnAddMessage.setOnClickListener {
            mProgress.show()
            if (validateEmployeeData()) {

                mProgress.show()
                val attachmentBase64 = photo1?.let { ImageUtil.bitmapToBase64(it) } ?: ""
                Log.d("AddMessage", "Attachment Base64: $attachmentBase64")

                if (TeacherDetails().getRole(requireContext()).equals("Teacher")){
                    val roleName =  TeacherDetails().getRole(requireContext())
                    val teacherName =  TeacherDetails().getName(requireContext())
                    val roleAndName = "$teacherName ($roleName)"
                    teacherNameRef = roleAndName
                    isTeacherRole = false
                }
                if (isTeacherRole){
                    teacherNameRef = SchoolId().getLoginRole(requireContext())
                }

                val sendMessage = mapOf(
                    "recipient_type" to binding.spSendBy1.selectedItem.toString(),
                    "search_specific" to binding.AvailableData.selectedItem.toString(),
                    "message" to binding.edtwriteMessage.text.toString(),
                    "attachment" to ImageConverter().convertImageViewToBase64(binding.imageStudent),
                    "role" to teacherNameRef,
                    "school_id" to schoolId,
                    "employee_id" to TeacherDetails().getTeacherId(requireContext()),
                )

                Log.d("AddMessage", "Send Message Map: $sendMessage")

                Toast.makeText(context, "Message Sent successfully!", Toast.LENGTH_SHORT).show()

                // Show confirmation dialog

                viewModelSendMessage.sendMessage(sendMessage)
//                    binding.txtError.visibility=View.VISIBLE
                //   binding.txtError.visibility=View.VISIBLE
                mProgress.dismiss()
                showHomeWorkDialog()
            }
            /*  if (MethodLibrary().SpinnerValidation(binding.spSendBy2)){
                Toast.makeText(requireContext(), "True", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "False", Toast.LENGTH_SHORT).show()
            }*/
        }
    }
    private fun validateEmployeeData(): Boolean {
        mProgress.dismiss()

        val Specific = binding.spSendBy1.selectedItemPosition.toString().trim()
       // val classemp = binding.AvailableData.selectedItemPosition.toString().trim()
        val messgate = binding.edtwriteMessage.text.toString().trim()

        return when {
            Specific.isEmpty() || Specific == "0" -> {
                showToast("Select a valid Specific Class")
                false
            }

            messgate.isEmpty() -> {
                showError(binding.edtwriteMessage, "Enter Your Message Here")
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
    private fun resetAttachment() {
        // Reset the background drawable and text
        binding.textAttach.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.global_button_background
            )
        ) // Change to a default color (e.g., gray)
        binding.textAttach.text = "Attach Image" // Reset the button text

        // Optionally reset the text color
        binding.textAttach.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.global_cardview_background
            )
        )

        // Reset the photo1 variable to null to clear the image
        photo1 = null
    }

    private fun showImagePickerDialog(requestImageCapture: Int, requestImageGallery: Int) {
//        val options = arrayOf("Camera", "Choose from Gallery")
//        AlertDialog.Builder(requireContext())
//            .setTitle("Pick an Image")
//            .setItems(options) { _, which ->
//                when (which) {
//                    0 -> captureImage(requestImageCapture)  // Capture image from camera
//                    1 -> openGallery(requestImageGallery)   // Choose image from gallery
//                }
//            }
//            .show()
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

//    private fun openGallery(requestGallery: Int) {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
//        startActivityForResult(intent, requestGallery)
//    }
//
//    private fun captureImage(IMAGE_CAPTURE_CODE: Int) {
//        try {
//            if (ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.CAMERA
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(intent, IMAGE_CAPTURE_CODE)
//            } else {
//                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        try {
//            when (requestCode) {
//                REQUEST_IMAGE_CAPTURE_1 -> {
//                    val photo = data?.extras?.get("data") as? Bitmap
//                    photo?.let {
//                        Log.d("onActivityResult", "Captured Image Bitmap: $it")
//
//                        binding.textAttach.setBackgroundDrawable(BitmapDrawable(resources, it))
//
//                        binding.textAttach.text = "Image Attached"
//
//                        binding.textAttach.setBackgroundColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                R.color.green
//                            )
//                        )
//
//                        binding.textAttach.setTextColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                R.color.white
//                            )
//                        )
//
//                        photo1 = it // Save the captured photo for later Base64 conversion
//                    }
//                }
//
//                REQUEST_IMAGE_GALLERY_1 -> {
//                    data?.data?.let { selectedImageUri ->
//                        try {
//                            val photo = MediaStore.Images.Media.getBitmap(
//                                requireContext().contentResolver,
//                                selectedImageUri
//                            )
//                            Log.d("onActivityResult", "Selected Image Bitmap: $photo")
//
//                            binding.textAttach.setBackgroundDrawable(
//                                BitmapDrawable(
//                                    resources,
//                                    photo
//                                )
//                            )
//
//                            binding.textAttach.text = "Image Attached"
//
//                            binding.textAttach.setBackgroundColor(
//                                ContextCompat.getColor(
//                                    requireContext(),
//                                    R.color.green
//                                )
//                            )
//
//                            binding.textAttach.setTextColor(
//                                ContextCompat.getColor(
//                                    requireContext(),
//                                    R.color.white
//                                )
//                            )
//
//                            photo1 = photo // Save the selected photo for later Base64 conversion
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
           // captureImage(REQUEST_IMAGE_CAPTURE_1)
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditMessage(message: GetMessageData) {

        var data = mapOf<String, String>(
            "id" to message.id.toString(),
            "role" to message.role,
            "search_specific" to message.search_specific.toString(),
            "recipient_type" to message.recipient_type.toString(),
            "attachment" to message.attachment.toString(),
            "message" to message.message.toString(),
            "created_at" to message.created_at.toString(),
        )
        toolBox.sendDataToFragment(requireContext(), EditMessage(), data)

//        val messageSchoolId = message.school_id.trim()
//        val Id = message.id
//
//        val bundle = Bundle().apply {
//            putParcelable("Message", message) // Pass the employee data as arguments
//           /* putString("school_id", messageSchoolId) // Pass schoolId
//            putString("id", Id) // Pass employeeId*/
//        }
//
//        val editEmpFragment = EditMessage().apply {
//            arguments = bundle
//        }
//
//        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, editEmpFragment)
//            .addToBackStack(null)
//            .commit()
    }



    override fun onDeleteMessage(message: GetMessageData, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete the message?")
            .setPositiveButton("Yes") { dialog, _ ->

                val schoolId = message.school_id
                val Id = message.id

                if (Id != null) {
                    // Call the ViewModel method to delete the homework
                    viewModelGetmessage.deleteHomeWork(schoolId, Id)

                    // Remove the item from the adapter's data list
                    getmessageadapter.removeItemAt(position)

                    Toast.makeText(
                        requireContext(),
                        "HomeWork deleted successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss() // Close the dialog
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: Homework ID is missing.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    fun submissionData(){
        val attachmentBase64 = photo1?.let { ImageUtil.bitmapToBase64(it) } ?: ""
        Log.d("AddMessage", "Attachment Base64: $attachmentBase64")

        val sendMessage = mapOf(
            "recipient_type" to binding.spSendBy1.selectedItem.toString(),
            "search_specific" to binding.AvailableData.selectedItem.toString(),
            "message" to binding.edtwriteMessage.text.toString(),
            "attachment" to attachmentBase64,
            "school_id" to SchoolId().getSchoolId(requireContext())
        )

        Log.d("AddMessage", "Send Message Map: $sendMessage")

        Toast.makeText(context, "Message Sent successfully!", Toast.LENGTH_SHORT).show()
        binding.layoutSendmessage.visibility = GONE

        // Show confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Message Has Been Sent.")
            .setMessage(binding.edtwriteMessage.text.toString())
            .setPositiveButton("Ok") { dialog, _ ->
                // Reset the image attachment and text
                resetAttachment()
                initobserval()

                binding.edtwriteMessage.text.clear()
                dialog.dismiss()
            }
            .create()
            .show()

        // Call the view model to send the message
        viewModelSendMessage.sendMessage(sendMessage)
    }
    private fun showHomeWorkDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Message Sent Successfully",
            message = "Would you like to send more messages?",
            positiveButtonText = "YES",
            positiveButtonAction = { toolBox.fragmentChanger(SendMessageAdmin(), requireContext()) },
            negativeButtonText = "No",
            negativeButtonAction = {toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
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

}