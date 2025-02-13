package com.example.schoolerp.Fragments

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentEditEmpBinding
import com.example.schoolerp.repository.AllEmployeesRepository
import com.example.schoolerp.util.ImageUtil
import com.example.schoolerp.viewmodel.AllEmployeesViewModel
import com.example.schoolerp.viewmodelfactory.AllEmployeesViewModelFactory
import com.example.student.FullScreenImageActivity
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EditEmp : Fragment() {
    private lateinit var binding: FragmentEditEmpBinding
    private lateinit var viewModel: AllEmployeesViewModel
    var tooBox = MethodLibrary()
    private val PERMISSION_REQUEST_CODE = 103
    private val calendar = Calendar.getInstance()


    companion object {
        const val REQUEST_IMAGE_CAPTURE_1: Int = 1
        const val REQUEST_IMAGE_GALLERY_1: Int = 11
    }

    var photo1: Bitmap? = null
    private lateinit var mProgress: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditEmpBinding.bind(inflater.inflate(R.layout.fragment_edit_emp, null))
        val apiService = RetrofitHelper.getApiService()
        val repository = AllEmployeesRepository(apiService)
        val factory = AllEmployeesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AllEmployeesViewModel::class.java)

        arguments?.getParcelable<AllEmployee>("employee")?.let { employee ->
            populateFields(employee)

        }
        setupDatePickers()
        setupListeners()
        mProgress = createProgressDialog()
        setupMobileNumberValidation()

        return binding.root
    }
    private fun createProgressDialog(): ProgressDialog {
        return ProgressDialog(requireContext()).apply {
            setMessage("Please wait...")
            setCancelable(false)
        }
    }

    private fun populateFields(employee: AllEmployee) {
        binding.etEmployeesName.setText(employee.employee_name ?: "")
        binding.edtMobileNumber.setText(employee.number ?: "")
        binding.edtdateofjoining.setText(employee.date_of_joining ?: "")
        binding.edtMonthlysalary.setText(employee.monthly_salary ?: "")
        binding.edtfatherhasband.setText(employee.f_h_name ?: "")
        binding.edtedacation.setText(employee.education ?: "")
        binding.edtexperience.setText(employee.experience ?: "")
        binding.edtdateofbirth.setText(employee.date_of_birth ?: "")
        binding.edthomeaddress.setText(employee.home_address ?: "")
        binding.edtnationalId.setText(employee.national_id ?: "")
        binding.edtemailaddres.setText(employee.email ?: "")
        setSpinnerSelection(binding.spEmployeeStatus,employee.st_status)
        setSpinnerSelection(binding.edtgender, employee.gender)
        setSpinnerSelection(binding.edtemployeesroll, employee.employee_role)
        setSpinnerSelection(binding.edtreligion, employee.religion)
        setSpinnerSelection(binding.edtbloodgroup, employee.blood_group)

       // MethodLibrary().displayImage("${ImageUtil.BASE_URL_IMAGE}employee/${employee.picture}", binding.imageEmp , binding.root.context)

        val imageUrl = ImageUtil.getFullImageUrl("employee", employee.picture.toString())

        // Load the image using Glide
        Glide.with(binding.root.context)
            .load(imageUrl)
            .error(R.drawable.imagenotfound)
            .into(binding.imageEmp)

        binding.imageEmp.setOnClickListener {
            val context = binding.imageEmp.context
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                putExtra("image_url", imageUrl)
            }
            context.startActivity(intent)
        }


    }
                private fun setSpinnerSelection(spinner: Spinner, value: String?) {
                    val adapter = spinner.adapter
                    if (adapter != null && value != null) {
                        for (i in 0 until adapter.count) {
                            if (adapter.getItem(i).toString() == value) {
                                spinner.setSelection(i)
                                break
                            }
                        }
                    }
                }
    private fun removeImage(imageView: ImageView) {
        imageView.setImageDrawable(null)
    }

    private fun setupListeners() {
        binding.edtChooseFile.setOnClickListener {
            //removeImage(binding.imageEmp)

            //  openImageChooser()
            showImagePickerDialog(
                REQUEST_IMAGE_CAPTURE_1, REQUEST_IMAGE_GALLERY_1
            )

        }

        binding.btnSubmit.setOnClickListener {
            mProgress.show()

            // Validate employee data before proceeding
            if (validateEmployeeData()) {
                tooBox.startLoadingBar("Loading...",cancelable = true,requireContext())
                // Retrieving mobile number from the EditText and trimming whitespace
                val mobileNumber = binding.edtMobileNumber.text.toString().trim()

                // Checking the length of the name and mobile number before generating password
                val password =
                    if (binding.etEmployeesName.text.toString().length >= 3 && mobileNumber.length >= 7) {
                        // Create a password using the first 3 characters of the name and the last 7 characters of the mobile number
                        binding.etEmployeesName.text.toString().trim()
                            .substring(0, 3) + mobileNumber.substring(6)
                    } else {
                        "defaultPassword"  // Default password if the conditions are not met
                    }

                // Retrieve SharedPreferences for school ID
                val sharedPreferences = requireActivity().getSharedPreferences(
                    "onboarding_prefs",
                    AppCompatActivity.MODE_PRIVATE
                )
                val schoolId = sharedPreferences.getString("school_id", "") ?: ""

                val employeeId =
                    arguments?.getString("employee_id")?.toIntOrNull() ?: return@setOnClickListener

                // Get the attachmentBase64 string (optional part, for image attachment if available)
               // val attachmentBase64 = photo1?.let { ImageUtil.bitmapToBase64(it) } ?: ""
                val attachmentBase64 = photo1?.let {
                    ImageUtil.bitmapToBase64(it) // Convert Bitmap to Base64 if photo1 is available
                } ?: ImageUtil.getBase64StringFromImageView(binding.imageEmp) // Fallback to ImageView if photo1 is null

                // Prepare the data map for employee update request
                val editEmployeeData = mapOf(
                    "employee_name" to binding.etEmployeesName.text.toString(),
                    "number" to binding.edtMobileNumber.text.toString(),
                    "employee_role" to binding.edtemployeesroll.selectedItem.toString(),
                    "picture" to attachmentBase64,
                    "date_of_joining" to binding.edtdateofjoining.text.toString(),
                    "monthly_salary" to binding.edtMonthlysalary.text.toString(),
                    "f_h_name" to binding.edtfatherhasband.text.toString(),
                    "education" to binding.edtedacation.text.toString(),
                    "gender" to binding.edtgender.selectedItem.toString(),
                    "blood_group" to binding.edtbloodgroup.selectedItem.toString(),
                    "experience" to binding.edtexperience.text.toString(),
                    "date_of_birth" to binding.edtdateofbirth.text.toString(),
                    "home_address" to binding.edthomeaddress.text.toString(),
                    "national_id" to binding.edtnationalId.text.toString(),
                    "email" to binding.edtemailaddres.text.toString(),
                    "religion" to binding.edtreligion.selectedItem.toString(),
                    "school_id" to schoolId,
                    "st_status" to binding.spEmployeeStatus.selectedItem.toString(),
                    "username" to mobileNumber,
                    "password" to password,
                    "id" to employeeId.toString()
                )

                // Log the request data for debugging purposes
                Log.d("EditEmployeeRequest", "Request Data: $editEmployeeData")

                // Pass the data to the ViewModel for editing the employee
                viewModel.editEmployee(editEmployeeData)
//                showPreviousDataDialog()

                // Observe the API response for success/failure
                viewModel.apiResponse.observe(viewLifecycleOwner) { response ->
                    tooBox.stopLoadingBar()
                    if (response.status.equals("success")){
                        tooBox.showConfirmationDialog(
                            context = requireContext(), // or requireContext() if in a Fragment
                            title = "Data Updated Successfully",
                            message = "Would you like to view the employee list?",
                            positiveButtonText = "YES",
                            positiveButtonAction = { tooBox.fragmentChanger(AllEmployees(), requireContext()) },
                            negativeButtonText = "NO",
                            negativeButtonAction = { tooBox.activityChanger(MainActivity(), requireContext()) },
                            cancelable = false)
                    }else{
                        tooBox.showConfirmationDialog(
                            context = requireContext(),
                            title = "Warning !",
                            message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                            positiveButtonText = "OK",
                            positiveButtonAction = { tooBox.activityChanger(MainActivity(), requireContext()) },
                            negativeButtonText = "",
                            negativeButtonAction = { },
                            cancelable = false
                        )
                    }

                    mProgress.dismiss()
                }
            }
        }
    }

        private fun showPreviousDataDialog() {
            // Create an AlertDialog
            val builder = AlertDialog.Builder(requireContext())

            // Set the title of the dialog
            builder.setTitle("Data Updated Successfully")

            // Set the message or options for the user
            builder.setMessage("Would you like to add more data or view the employee list?")

            // Add "Show the List" option to allow the user to see all employee roles
            builder.setPositiveButton("Show the List") { dialog, which ->
                // Replace the current fragment with AllEmployeesFragment
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(
                    R.id.fragment_container,
                    AllEmployees()
                ) // Replace with the correct container ID
                transaction.addToBackStack(null)  // Add the transaction to the back stack if you want to navigate back to the previous fragment
                transaction.commit()
            }

            // Add "Add More" option to clear the fields and allow the user to fill new data
            builder.setNegativeButton("Add More") { dialog, which ->
                // Clear all form fields when the "Add More" option is selected
              //  clearFormFields()

                // Optionally dismiss the dialog
                dialog.dismiss()
            }

            builder.create().show()
        }


        private fun clearFormFields() {
            // Clear each input field (you can add other fields if needed)
            binding.edtemployeesroll.setSelection(0) // Reset Spinner to first item
            binding.etEmployeesName.text?.clear() // Clear Employee Name
            binding.edtMobileNumber.text?.clear() // Clear Mobile Number
            binding.edtdateofjoining.text?.clear() // Clear Date of Joining
            binding.edtMonthlysalary.text?.clear() // Clear Monthly Salary
            binding.edtfatherhasband.text?.clear() // Clear Father's/ Husband's Name
            binding.edtedacation.text?.clear() // Clear Education
            binding.edtgender.setSelection(0) // Reset Gender Spinner
            binding.edtbloodgroup.setSelection(0) // Reset Blood Group Spinner
            binding.edtexperience.text?.clear() // Clear Experience
            binding.edtdateofbirth.text?.clear() // Clear Date of Birth
            binding.edthomeaddress.text?.clear() // Clear Home Address
            binding.edtemailaddres.text?.clear() // Clear Email Address
            binding.edtnationalId.text?.clear() // Clear National ID
        }

    private fun setupMobileNumberValidation() {
        binding.edtMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val mobileNumber = editable.toString().trim()

                if (mobileNumber.length == 10 && mobileNumber.matches(Regex("\\d{10}"))) {
                    // Change the border color of the TextInputLayout to green when valid
                    binding.textInputLayoutMobileNumber.setBoxStrokeColor(
                        ContextCompat.getColor(
                            context!!, R.color.green
                        )
                    )
                } else {
                    // Reset the border color if the number is invalid
                    binding.textInputLayoutMobileNumber.setBoxStrokeColor(
                        ContextCompat.getColor(
                            context!!, R.color.red
                        )
                    )
                }
            }
        })
    }


    private fun validateEmployeeData(): Boolean {
        mProgress.dismiss()

        val name = binding.etEmployeesName.text.toString().trim()
        val mobileNumber = binding.edtMobileNumber.text.toString().trim()
        val role = binding.edtemployeesroll.selectedItemPosition
        val dateOfJoining = binding.edtdateofjoining.text.toString().trim()
        val salary = binding.edtMonthlysalary.text.toString().trim()
        val fHName = binding.edtfatherhasband.text.toString().trim()
        val education = binding.edtedacation.text.toString().trim()
        val gender = binding.edtgender.selectedItemPosition
        val dob = binding.edtdateofbirth.text.toString().trim()
        val address = binding.edthomeaddress.text.toString().trim()
        val nationalId = binding.edtnationalId.text.toString().trim()
        val religion = binding.edtreligion.selectedItemPosition
        val emailaddress = binding.edtemailaddres.text.toString().trim()

        return when {
            name.isEmpty() -> {
                showError(binding.etEmployeesName, "Employee Name is required")
                false
            }
            name.length < 3 || !name.matches(Regex("^[A-Za-z ]{3,}$")) -> {
                showError(binding.etEmployeesName, "Name must contain at least three alphabetic characters")
                false
            }
            mobileNumber.isEmpty() || !mobileNumber.matches(Regex("\\d{10}")) -> {
                showError(binding.edtMobileNumber, "Enter a valid 10-digit Mobile Number")
                false
            }
            role == 0 -> { // Assuming 0 is the default "Select Role" position
                showToast("Select a valid Employee Role")
                false
            }
            dateOfJoining.isEmpty() -> {
                showError(binding.edtdateofjoining, "Enter Date of Joining")
                false
            }
            salary.isEmpty() -> {
                showError(binding.edtMonthlysalary, "Enter Monthly Salary")
                false
            }
            fHName.isEmpty() -> {
                showError(binding.edtfatherhasband, "Enter Father's/Husband's Name")
                false
            }
            education.isEmpty() -> {
                showError(binding.edtedacation, "Enter Education")
                false
            }
            gender == 0 -> { // Assuming 0 is the default "Select Gender" position
                showToast("Select a valid Gender")
                false
            }
            dob.isEmpty() -> {
                showError(binding.edtdateofbirth, "Enter Date of Birth")
                false
            }
            address.isEmpty() -> {
                showError(binding.edthomeaddress, "Enter Home Address")
                false
            }
            nationalId.isEmpty() -> {
                showError(binding.edtnationalId, "Enter Aadhaar Id")
                false
            }
            religion == 0 -> { // Assuming 0 is the default "Select Religion" position
                showToast("Select a valid Religion")
                false
            }
            emailaddress.isEmpty() -> {
                showError(binding.edtemailaddres, "Enter Email Address")
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
    private fun showImagePickerDialog(requestImageCapture: Int, requestImageGallery: Int) {
        val options = arrayOf("Camera", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Pick an Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureImage(requestImageCapture)
                    1 -> openGallery(requestImageGallery)
                }
            }
            .show()
    }

    private fun captureImage(IMAGE_CAPTURE_CODE: Int) {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, IMAGE_CAPTURE_CODE)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery(requestGallery: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, requestGallery)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                SendMessageAdmin.REQUEST_IMAGE_CAPTURE_1 -> {
                    val photo = data?.extras?.get("data") as? Bitmap
                    photo?.let {
                        Log.d("onActivityResult", "Captured Image Bitmap: $it")
                        photo1 = it // Save the captured photo for later Base64 conversion
                        binding.imagePreview.setImageBitmap(it)
                        binding.imageEmp.visibility = View.GONE
                        binding.imagePreview.visibility = View.VISIBLE
                    } ?: run {
                        binding.imageEmp.setImageResource(R.drawable.imagenotfound) // Placeholder
                    }
                }

                SendMessageAdmin.REQUEST_IMAGE_GALLERY_1 -> {
                    data?.data?.let { selectedImageUri ->
                        try {
                            val photo = MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                selectedImageUri
                            )
                            Log.d("onActivityResult", "Selected Image Bitmap: $photo")
                            photo1 = photo // Save the selected photo for later Base64 conversion
                            binding.imagePreview.setImageBitmap(photo)
                            binding.imageEmp.visibility = View.GONE
                            binding.imagePreview.visibility = View.VISIBLE
                        } catch (e: IOException) {
                            e.printStackTrace()
                            binding.imageEmp.setImageResource(R.drawable.imagenotfound) // Placeholder
                        }
                    } ?: run {
                        binding.imageEmp.setImageResource(R.drawable.imagenotfound) // Placeholder
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.imageEmp.setImageResource(R.drawable.imagenotfound) // Placeholder
        }
    }

    // Handle configuration changes or app restart
    override fun onResume() {
        super.onResume()

        if (photo1 == null) {
            binding.imageEmp.setImageResource(R.drawable.imagenotfound) // Placeholder
        } else {
            binding.imageEmp.setImageBitmap(photo1)
        }
    }


    private fun setupDatePickers() {
        binding.edtdateofjoining.setOnClickListener {
            showDatePickerDialog { date -> binding.edtdateofjoining.setText(date) }
        }

        binding.edtdateofbirth.setOnClickListener {
            showDatePickerDialog { date -> binding.edtdateofbirth.setText(date) }
        }
    }
    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(calendar.apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }.time)

            onDateSet(formattedDate)
        }, year, month, day).show()
    }


}

