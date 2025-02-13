package com.example.schoolerp.Fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.helpers.ImageConverter
import com.example.helpers.MethodLibrary
import com.example.helpers.validation
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.ClassWithSubjects
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentAddHomeworkBinding
import com.example.schoolerp.repository.AddHomeworkRepository
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.getSubjectRepository
import com.example.schoolerp.viewmodel.AddHomeworkViewModel
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.getSubjectViewModel
import com.example.schoolerp.viewmodelfactory.AddHomeworkViewModelFactoryclass
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.getSubjectViewModelFactory
import com.example.teacher.TeacherDetails
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddHomework : Fragment() {
    private lateinit var binding: FragmentAddHomeworkBinding
    private lateinit var viewmodelAllClass: AllClassViewModel
    private var toolBox = MethodLibrary()
    private var validator = validation()
    private  var isPresentSubject:Boolean = false
    private lateinit var viewModelAddHome: AddHomeworkViewModel
    private lateinit var viewModel: getSubjectViewModel
    private lateinit var classID:String
    private lateinit var subjectID:String
    private lateinit var imageView: ImageView

    private lateinit var teacherNameRef:String
    private var isTeacherRole:Boolean = true

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200

    private var currentPhotoPath: String? = null

    private lateinit var speechRecognizer: SpeechRecognizer
    private val REQUEST_RECORD_AUDIO_PERMISSION = 100
    private var permissionGranted = false

    private var massageSave:String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddHomeworkBinding.inflate(layoutInflater)
        initializeViewModels()
        getClasses()
        activeEvent()

        Glide.with(this)
            .load(R.drawable.mic_active)
            .into(binding.micIsActive);
        activeVoiceToText()

        return binding.root
    }



//    ========== Voice to Text ====================
    private  fun activeVoiceToText(){
// Initialize SpeechRecognizer
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            // Mic is activated
            showToast("Mic is activated")
        }

        override fun onBeginningOfSpeech() {
            // User started speaking
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Sound level changed
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // Audio buffer received
        }

        override fun onEndOfSpeech() {
            // Mic is deactivated
            showToast("Mic is deactivated")
        }

        override fun onError(error: Int) {
            // Handle errors
            when (error) {
                SpeechRecognizer.ERROR_AUDIO -> showMessage("Audio recording error")
                SpeechRecognizer.ERROR_CLIENT -> showMessage("Client side error")
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> showMessage("Insufficient permissions")
                SpeechRecognizer.ERROR_NETWORK -> showMessage("Network error")
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> showMessage("Network timeout")
                SpeechRecognizer.ERROR_NO_MATCH -> showMessage("No match found")
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> showMessage("Recognition service busy")
                SpeechRecognizer.ERROR_SERVER -> showMessage("Server error")
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> showMessage("No speech input")
                else -> showMessage("Unknown error")
            }
        }

        override fun onResults(results: Bundle?) {
            // Handle recognized text
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val recognizedText = matches[0]
                val currentText = binding.edtHomeWorkDetail.text.toString()
                // Concatenate the recognized text with the current text
                binding.edtHomeWorkDetail.setText(currentText + " " + recognizedText)
                massageSave += recognizedText
            }
        }


        override fun onPartialResults(partialResults: Bundle?) {
            // Partial results are available
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            // Reserved for future use
        }
    })

// Check and request permissions
    if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    } else {
        permissionGranted = true
    }
        // Start listening when a button is clicked
        binding.startButton.setOnClickListener {
            //binding.edtHomeWorkDetail.setText(massageSave)
            startVoiceRecognition()
        }
    }

    // Function to show Toast messages
    private fun showToast(message: String) {
        if ("Mic is activated" == message) {
            binding.micIsActive.visibility = View.VISIBLE
        } else if ("Mic is deactivated" == message) {
            binding.micIsActive.visibility = View.GONE
        }
        //Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy() // Release resources
    }

    private fun showMessage(message: String) {
        // Display a message (e.g., using a Toast)
        binding.micIsActive.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun startVoiceRecognition() {
        if (permissionGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            speechRecognizer.startListening(intent)
        } else {
            showMessage("Microphone permission is required")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

//    =========== Voice to Text ===================

    private fun initializeViewModels(){
        imageView = binding.imageStudent

        val factory = AllClassViewModelFactory(AllClassRepository())
        viewmodelAllClass = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)

        val apiService = RetrofitHelper.getApiService()
        val repository = getSubjectRepository(apiService)
        viewModel = ViewModelProvider(this, getSubjectViewModelFactory(repository, SchoolId().getSchoolId(requireContext()))).get(getSubjectViewModel::class.java)


        val apiService1 = RetrofitHelper.getApiService()
        val repository1 = AddHomeworkRepository(apiService1)
        val factory1 = AddHomeworkViewModelFactoryclass(repository1)
        viewModelAddHome = ViewModelProvider(this, factory1).get(AddHomeworkViewModel::class.java)
    }

    private fun getClasses(){
        try {
            viewmodelAllClass.getClasses(SchoolId().getSchoolId(requireContext())).observe(viewLifecycleOwner) { response ->
                if (response != null && response.status) {
                    val classNames = response.data.map { it.class_name }
                    val classIds = response.data.map { it.class_id }

                    val classAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,classNames)
                    binding.spSelectClass.adapter = classAdapter

                    binding.spSelectClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>,view: View?,position: Int, id: Long) {
                            val selectedClassId = classIds[position]
                            getSpinnerSubject(classIds[position])
                            Log.d("AddHomework", "Selected Class ID: $selectedClassId")


                            classID = classIds[position]

                            //Toast.makeText(requireContext(),classIds[position], Toast.LENGTH_SHORT).show()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                        }
                    }

                } else {
                    Log.d("AddHomework", "No classes found")
                }
            }

        }catch (e:Exception){
            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSpinnerSubject(ClassId: String) {
        viewModel.classWithSubjects.observe(viewLifecycleOwner) { classList ->
            // Find the class with the given ClassId
            val selectedClass = classList.find { it.classes == ClassId }

            if (selectedClass != null && selectedClass.subjects.isNotEmpty()) {
                isPresentSubject = false
                binding.spSelectSubjectLayout.visibility = View.VISIBLE


                // Map the subjects to a list of SubjectItem (id, subject_name)
                val subjectItems = selectedClass.subjects.map { SubjectItem(it.id, it.subject_name) }

                //Toast.makeText(requireContext(), subjectItems[2].id, Toast.LENGTH_SHORT).show()

                // Create an ArrayAdapter with the subject names
                val spinnerAdapterSubject = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    selectedClass.subjects.map { it.subject_name }
                )
                spinnerAdapterSubject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spSelectSubject.adapter = spinnerAdapterSubject

                // Handle item selection in the Spinner
                binding.spSelectSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        //val selectedSubjectId = subjectItems[position].id
                        subjectID = subjectItems[position].id
                        //println("Selected subject ID: $selectedSubjectId")
                        //Toast.makeText(requireContext(), selectedSubjectId, Toast.LENGTH_SHORT).show()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            } else {
                binding.spSelectSubjectLayout.visibility = View.GONE
                isPresentSubject = true
              //  Toast.makeText(requireContext(), "No subjects found for class $ClassId", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Define a data class to hold the id and subject name
    data class SubjectItem(val id: String, val subjectName: String)

    private fun activeEvent(){
        binding.btnAddMessage.setOnClickListener {
            if (
                binding.edtDateHomeWork.text?.isNotEmpty() == true &&
                binding.edtHomeWorkDetail.text?.isNotEmpty() == true

                ){

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

                if (!isPresentSubject){
                    val homeworkData = mapOf(
                        "homework_detail" to binding.edtHomeWorkDetail.text.toString().trim(),
                        "classes" to classID,
                        "homework_date" to binding.edtDateHomeWork.text.toString(),
                        // "set_by" to "$teacher (${SchoolId().getLoginRole(requireContext())})",
                        "set_by" to teacherNameRef,
                        "school_id" to SchoolId().getSchoolId(requireContext()),
                        "subject" to subjectID,
                        "attachment" to ImageConverter().convertImageViewToBase64(binding.imageStudent),
                        "employee_id" to TeacherDetails().getTeacherId(requireContext())
                    )
                    //Toast.makeText(requireContext(), binding.edtDateHomeWork.text.toString(), Toast.LENGTH_SHORT).show()
                    viewModelAddHome.addHomework(homeworkData)

                    toolBox.showConfirmationDialog(
                        context = requireContext(),
                        title = "Homework Added Successfully",
                        message = "Would You Like to Add Other Homework?",
                        positiveButtonText = "Yes",
                        positiveButtonAction = {
                            toolBox.fragmentChanger(AddHomework(), requireContext())
                        },
                        negativeButtonText = "No",
                        negativeButtonAction = {
                            toolBox.fragmentChanger(DashBoard(), requireContext())
                        },
                        cancelable = false // Ensure the dialog cannot be dismissed by tapping outside
                    )
                    Log.d("SearchHomeWork", "Homework data as Map: $homeworkData")
                }else{
                    Toast.makeText(requireContext(), "No subjects found for class", Toast.LENGTH_SHORT).show()
                }
            }else{
                validator.errorLayout(binding.edtDateHomeWorkLayout, "Please enter date")
            }
        }

        binding.edtDateHomeWork.setOnClickListener {
            toolBox.DatePicker(binding.edtDateHomeWork,requireContext())
        }

        binding.edtDateHomeWorkLayout.setOnClickListener {
            toolBox.DatePicker(binding.edtDateHomeWork,requireContext())
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