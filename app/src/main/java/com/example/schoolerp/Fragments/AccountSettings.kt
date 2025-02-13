package com.example.schoolerp.Fragments.Fragment

import android.app.Activity
import android.app.KeyguardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.helpers.ImageConverter
import com.example.helpers.MethodLibrary
import com.example.helpers.validation
import com.example.onboardingschool.LoginPage
import com.example.principle.PrincipalDetails
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.InstituteProfileDataClass
import com.example.schoolerp.Fragments.SearchStudent
import com.example.schoolerp.Fragments.SearchStudent.Companion
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentAccountSettingsBinding
import com.example.schoolerp.models.responses.InstituteProfileResponse
import com.example.schoolerp.models.responses.LogoutResponse
import com.example.schoolerp.models.responses.passwordResponse
import com.example.schoolerp.repository.GeneralSettingRepository
import com.example.schoolerp.repository.InstituteProfileRepository
import com.example.schoolerp.viewmodel.GeneralSettingViewModel
import com.example.schoolerp.viewmodel.InstituteProfileViewModel
import com.example.schoolerp.viewmodelfactory.GeneralSettingViewModelFactory
import com.example.schoolerp.viewmodelfactory.InstituteProfileViewModelFactory
import com.example.teacher.TeacherDetails
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSettings : Fragment() {
    private lateinit var binding: FragmentAccountSettingsBinding
    private lateinit var studentsp: String
    private lateinit var viewModel: GeneralSettingViewModel
    private val toolbox = MethodLibrary()
    private val InstituteLogo: String = "assetsNew/img/institute_logos/"

    private var stid: String = ""
    private var schoolName: String = ""
    private var password: String = ""
    private var number: String = ""
    private var createdAt: String = ""
    private var schoolId: String = ""
    private var role: String = ""
    private var email: String = ""

    private val instituteViewModel: InstituteProfileViewModel by viewModels {
        InstituteProfileViewModelFactory(InstituteProfileRepository(RetrofitHelper.getApiService()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountSettingsBinding.bind(inflater.inflate(R.layout.fragment_account_settings, null))

        if (!toolbox.isInternetAvailable(requireContext())){
            Toast.makeText(requireContext(), "Device Offline", Toast.LENGTH_SHORT).show()
            toolbox.showSetText(toolbox.getDataString("username", requireContext()), binding.txtUsername)
            toolbox.showSetText(toolbox.getDataString("password", requireContext()), binding.txtPassword)
            toolbox.showSetText(toolbox.getDataString("role", requireContext()), binding.txtDestination)
            toolbox.showSetText(toolbox.getDataString("update", requireContext()), binding.txtActivationDate)
        }else{
            fetchInstituteProfile(SchoolId().getSchoolId(requireContext()))

        }
        binding.btnUpdatenumberupdate.setOnClickListener{
            submitInstituteData()
        }

        validation().setupPhoneNumberValidation(binding.user, binding.layoutChangeUser)

        observeViewModel()
        return binding.root
    }


    companion object {
        const val REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 12345
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set up the ViewModel
        val repository = GeneralSettingRepository(RetrofitHelper.getApiService(), SchoolId().getSchoolId(requireContext()))
        val viewModelFactory = GeneralSettingViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GeneralSettingViewModel::class.java]

        // Observe data
        viewModel.accountSettings.observe(viewLifecycleOwner) { response ->
            try {
                if (response.status){

                    if (SchoolId().getLoginRole(requireContext()).equals("Admin"))
                    {
                        response.data.forEach {
                            toolbox.showSetText(it.number.toString() ,binding.txtUsername,)
                            toolbox.showSetText(it.password.toString() ,binding.txtPassword,)
                            toolbox.showSetText(it.role.toString() ,binding.txtDestination,)
                            toolbox.showSetText(it.updated_at.toString().substring(0, 10) ,binding.txtActivationDate,)

                            //Save Locally
                            toolbox.saveDataString("username", it.number.toString(), requireContext())
                            toolbox.saveDataString("password", it.password.toString(), requireContext())
                            toolbox.saveDataString("role", it.role.toString(), requireContext())
                            toolbox.saveDataString("update", it.number.toString(), requireContext())

                            password = it.password
                            number = it.number
                            role = it.role
                            createdAt = it.updated_at
                            schoolId = it.school_id
                            updateUI()
                            //Toast.makeText(requireContext(), "Admin", Toast.LENGTH_SHORT).show()
                        }

                    }else {
                        toolbox.showSetText(PrincipalDetails().getPrincipalNumber(requireContext()) ,binding.txtUsername)
                        toolbox.showSetText(PrincipalDetails().getPassword(requireContext()) ,binding.txtPassword)
                        toolbox.showSetText(PrincipalDetails().getRole(requireContext()) ,binding.txtDestination)
                        toolbox.showSetText(PrincipalDetails().getJoiningDate(requireContext()) ,binding.txtActivationDate)

                        password = PrincipalDetails().getPassword(requireContext())
                        number = PrincipalDetails().getPrincipalNumber(requireContext())
                        role = PrincipalDetails().getRole(requireContext())
                        createdAt = PrincipalDetails().getJoiningDate(requireContext())
                        schoolId = SchoolId().getSchoolId(requireContext())

                        updateUI()
                    }

                }else{
                    Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Error handling
        viewModel.error.observe(viewLifecycleOwner) { error ->
            //Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
        }

        // Set up the Delete Account button click listener
        view.findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
            openEmailWithAdminInfo()
        }

        // Fetch account settings
        viewModel.fetchAccountSettings()

        // Set up password reset button click listener
        view.findViewById<Button>(R.id.btnUpdateAccountSetting).setOnClickListener {
            if (!toolbox.isInternetAvailable(requireContext())){
                //toolbox.deviceOffLineMassage(requireContext())
            }else{
                validationUpdatePassword()
            }

        }
    }

    private fun updateUI() {
        try {
            binding.txtUsername.text = number
            binding.txtDestination.text = role
            var isPasswordVisible = false

            binding.txtPassword.text = "*".repeat(SchoolId().getPassword(requireContext()).length)

            binding.togglePasswordVisibility.setOnClickListener {
                isPasswordVisible = !isPasswordVisible

                if (isPasswordVisible) {

                    val keyguardManager = requireContext().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

                    if (keyguardManager.isKeyguardSecure) {
                        val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                            "Authenticate",
                            "Please enter your device password to view the password"
                        )
                        startActivityForResult(intent,
                            SearchStudent.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS
                        )
                    } else {
                        // No screen lock is set, show the password immediately
                        binding.txtPassword.text = SchoolId().getPassword(requireContext())
                        binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword) // Change to "eye-open" icon
                    }
//
//
//                // Show the actual password
//                binding.txtPassword.text = SchoolId().getPassword(requireContext())
//                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword) // Change to "eye-open" icon
                } else {
                    // Hide the password
                    binding.txtPassword.text = "*".repeat(SchoolId().getPassword(requireContext()).length)
                    binding.togglePasswordVisibility.setImageResource(R.drawable.eyeonpassword) // Change to "eye-closed" icon
                }

            }

            binding.txtActivationDate.text = createdAt.substring(0, 10)
        }catch (e: Exception){
            toolbox.showConfirmationDialog(
                context = requireContext(),
                title = "Warning !",
                message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                positiveButtonText = "OK",
                positiveButtonAction = { toolbox.activityChanger(MainActivity(), requireContext()) },
                negativeButtonText = "",
                negativeButtonAction = { },
                cancelable = false)
        }

    }

    private fun resetPassword(schoolId: String, password: String) {
        // Create a map of parameters
        val fields = mapOf(
            "school_id" to schoolId,
            "password" to password
        )

        // Make the API call using Retrofit
        RetrofitHelper.getApiService().updatePassword(fields).enqueue(object : Callback<passwordResponse> {
            override fun onResponse(call: Call<passwordResponse>, response: Response<passwordResponse>) {
                if (response.isSuccessful) {
                    val passwordResponse = response.body()
                    if (passwordResponse != null && passwordResponse.status) {
                        toolbox.clearSharedprepared("onboarding_prefs", requireContext())
                        toolbox.showConfirmationDialog(
                            context = requireContext(),
                            title = "Password has changed successfully",
                            message = "You have to login again with updated password",
                            positiveButtonText = "Login",
                            positiveButtonAction = { logoutUser() },
                            cancelable = false
                        )
                    } else {
                        binding.tvpasswordhint.text = passwordResponse?.message.toString()
                    }
                } else {
                    Toast.makeText(requireContext(), "Request failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<passwordResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logoutUser() {
        // Call the logout API
        RetrofitHelper.getApiService().logout().enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val logoutResponse = response.body()!!

                    if (logoutResponse.status) {
                        var sharedPreferences = requireContext().getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
                        // Clear SharedPreferences
                        sharedPreferences.edit().clear().apply()

                        // Show a toast message
                        Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()

                        toolbox.activityChanger(LoginPage(), requireContext())
                    } else {
                        Toast.makeText(requireContext(), logoutResponse.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to logout", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Logout failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openEmailWithAdminInfo() {
        val adminEmail = "hemantmarkhande590@gmail.com"
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            setPackage("com.google.android.gm")
            data = Uri.parse("mailto:$adminEmail")
        }

        if (emailIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Toast.makeText(requireContext(), "No Gmail app found. Please install it.", Toast.LENGTH_LONG).show()

            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm"))
            startActivity(playStoreIntent)
        }
    }

    private fun validationUpdatePassword() {
        val password = binding.password.text.toString()
        val passwordPattern = "(?=.[0-9])(?=.[!@#\$%^&*(),.?\":{}|<>])"

        if (password.trim().isNotEmpty()) {
            if (password.length >= 8) {
                if (password.contains(Regex(passwordPattern))) {
                    resetPassword(SchoolId().getSchoolId(requireContext()), password.trim())
                } else {
                    binding.layoutChangePassword.error = "Password must contain at least one number and one special character"
                }
            } else {
                binding.layoutChangePassword.error = "Password must be at least 8 characters"
            }
        } else {
            binding.layoutChangePassword.error = "Please enter a password"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SearchStudent.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == Activity.RESULT_OK) {
                binding.txtPassword.text = SchoolId().getPassword(requireContext())
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword)

            } else {
                // Authentication failed or canceled, keep the password hidden
                binding.txtPassword.text = "*".repeat(SchoolId().getPassword(requireContext()).length)
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeonpassword)

                Toast.makeText(context, "Authentication failed or canceled.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun fetchInstituteProfile(schoolId: String) {
        try {
            val apiService = RetrofitHelper.getApiService()
            val call = apiService.getInstituteProfile(schoolId)
            call.enqueue(object : Callback<InstituteProfileResponse> {
                override fun onResponse(
                    call: Call<InstituteProfileResponse>,
                    response: Response<InstituteProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        //=============== Successfully received response===========
                        val instituteProfile = response.body()
                        if (instituteProfile != null && !instituteProfile.data.institute_logo.isNullOrEmpty()) {

//                            Toast.makeText(requireContext(), instituteProfile.data.school_name, Toast.LENGTH_SHORT).show()
//                            Toast.makeText(requireContext(), instituteProfile.data.institute_address, Toast.LENGTH_SHORT).show()
//                            Toast.makeText(requireContext(), instituteProfile.data.tag_line, Toast.LENGTH_SHORT).show()
//                            Toast.makeText(requireContext(), instituteProfile.data.number, Toast.LENGTH_SHORT).show()
//                            Toast.makeText(requireContext(), instituteProfile.data.website, Toast.LENGTH_SHORT).show()
//                            Toast.makeText(requireContext(), instituteProfile.data.country, Toast.LENGTH_SHORT).show()
//                            Toast.makeText(requireContext(), instituteProfile.data.updated_at, Toast.LENGTH_SHORT).show()
//
                            binding.user.setText(instituteProfile.data.number)
                            binding.etNameOfInstitute.setText(instituteProfile.data.school_name)
                            binding.etWebsite.setText(instituteProfile.data.website)
                            binding.etTargetLine.setText(instituteProfile.data.tag_line)
                            binding.etAddress.setText(instituteProfile.data.institute_address)

                            toolbox.saveDataString("profileLogo", instituteProfile.data.institute_logo, requireContext())
                            toolbox.displayImage("${com.example.schoolerp.ImageUtil.ImageUtil.BASE_URL_IMAGE}$InstituteLogo${instituteProfile.data.institute_logo}", binding.SchoolLogo, requireContext())
                        } else {
                            println("Institute logo is null or empty")
                        }
                    } else {
                        println("Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<InstituteProfileResponse>, t: Throwable) {
                    t.printStackTrace()
                    println("API Call Failed: ${t.message}")
                }
            })
        }catch (e : Exception){
            toolbox.showConfirmationDialog(
                context = requireContext(),
                title = "Warning !",
                message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                positiveButtonText = "OK",
                positiveButtonAction = { toolbox.activityChanger(MainActivity(), requireContext()) },
                negativeButtonText = "",
                negativeButtonAction = { },
                cancelable = false)
        }
    }

    private fun submitInstituteData() {
        toolbox.startLoadingBar("Loading...", false, requireContext())
        val school_name = binding.etNameOfInstitute.text.toString().trim()
        val tag_line = binding.etTargetLine.text.toString().trim()
        val phone = binding.user.text.toString().trim()
        val website = binding.etWebsite.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val country = "India"
        val school_id = SchoolId().getSchoolId(requireContext())
        val imageLog = ImageConverter().convertImageViewToBase64(binding.SchoolLogo)

        Log.d(TAG, "Submitting Institute Data: nameOfInstitute=$school_name, phoneNumber=$phone, address=$address, imageLog=$imageLog")

        if (school_name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()) {
            instituteViewModel.submitInstitute(school_name, tag_line, phone, website, address, country, school_id, imageLog)
        } else {
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        instituteViewModel.instituteResponse.observe(viewLifecycleOwner) { response ->
            try {
                if (response?.status == true) {
                    toolbox.stopLoadingBar()
                    //Toast.makeText(requireContext(), "Institute added successfully", Toast.LENGTH_SHORT).show()
                    toolbox.showConfirmationDialog(requireContext(),
                        title = "Profile Updated Successfully",
                        positiveButtonText = "OK",
                        positiveButtonAction = { toolbox.activityChanger(MainActivity(), requireActivity()) },
                        cancelable = true
                    )
                    Log.d("Eeen", response?.status.toString())
                } else {
                    toolbox.stopLoadingBar()
                    Toast.makeText(requireContext(), "Failed to add institute", Toast.LENGTH_SHORT).show()
                }
            }catch (e : Exception){
                toolbox.showConfirmationDialog(
                    context = requireContext(),
                    title = "Warning !",
                    message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                    positiveButtonText = "OK",
                    positiveButtonAction = { toolbox.activityChanger(MainActivity(), requireContext()) },
                    negativeButtonText = "",
                    negativeButtonAction = { },
                    cancelable = false
                )
            }

        }

        instituteViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                toolbox.stopLoadingBar()
                //Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()

            }
        }
    }

}