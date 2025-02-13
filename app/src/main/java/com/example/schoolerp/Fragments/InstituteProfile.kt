package com.example.schoolerp.Fragments.Fragment


import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.helpers.ImageConverter
import com.example.helpers.MethodLibrary
import com.example.helpers.validation
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.Fragments.DashBoard
import com.example.schoolerp.ImageUtil.ImageUtil
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentInstituteProfileBinding
import com.example.schoolerp.models.responses.InstituteProfileResponse
import com.example.schoolerp.repository.InstituteProfileRepository
import com.example.schoolerp.viewmodel.InstituteProfileViewModel
import com.example.schoolerp.viewmodelfactory.InstituteProfileViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InstituteProfile : Fragment() {
    private var _binding: FragmentInstituteProfileBinding? = null
    private val binding get() = _binding!!
    var schoolId = ""
    val toolbox = MethodLibrary()
    val validation = validation()
    private val InstituteLogo: String = "assetsNew/img/institute_logos/"

    private val instituteViewModel: InstituteProfileViewModel by viewModels {
        InstituteProfileViewModelFactory(InstituteProfileRepository(RetrofitHelper.getApiService()))
    }

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstituteProfileBinding.inflate(inflater, container, false)

        if (!toolbox.isInternetAvailable(requireContext())){
            toolbox.deviceOffLineMassage(InstituteProfile(), requireContext())
        }else{
            validation.setupPhoneNumberValidation( binding.etPhoneNumber, binding.layoutNumber)
            toolbox.startLoadingBar("Loading...", false, requireContext())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button click listener for uploading logo
        binding.ivImageLog.setOnClickListener {
           openImageChooser()
            //ImagePickerHelper(requireContext(), binding.ivImageLog).openImageChooser()
        }

        // Button click listener for submitting data
        binding.btnSubmitInstituteProfile.setOnClickListener {
            if (!toolbox.isInternetAvailable(requireContext())){
                toolbox.deviceOffLineMassage(requireContext())
            }else{
                if (Validation()){
                    submitInstituteData()
                }
            }
        }


        binding.UpdateLogo.setOnClickListener {
            if (!toolbox.isInternetAvailable(requireContext())){
                toolbox.deviceOffLineMassage(requireContext())
            }else{
                    submitInstituteData()
            }
        }


        fetchInstituteProfile(SchoolId().getSchoolId(requireContext()))
//        loadInstituteProfile()
        observeViewModel()
    }

    private fun openImageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data
            selectedImageUri = data?.data
            binding.ivImageLog.setImageURI(selectedImageUri)
        }
    }

    private fun submitInstituteData() {
        toolbox.startLoadingBar("Loading...", false, requireContext())
        val school_name = binding.etNameOfInstitute.text.toString().trim()
        val tag_line = binding.etTargetLine.text.toString().trim()
        val phone = binding.etPhoneNumber.text.toString().trim()
        val website = binding.etWebsite.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val country = "India"
        val school_id = SchoolId().getSchoolId(requireContext())
        val imageLog = ImageConverter().convertImageViewToBase64(binding.ivImageLog)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadInstituteProfile(
        nameOfInstitute: String,
        targetLine: String,
        phoneNumber: String,
        website: String,
        address: String,
    ) {
        //      ----------------set Text From API--------------------------
        binding.etNameOfInstitute.setText(nameOfInstitute)
        binding.etTargetLine.setText(targetLine)
        binding.etPhoneNumber.setText(phoneNumber)
        binding.etWebsite.setText(website)
        binding.etAddress.setText(address)
    }

    private fun loadInstituteImage(imagePath:String) {
        toolbox.displayImage("${ImageUtil.BASE_URL_IMAGE}$InstituteLogo${imagePath}",binding.ivImageLog, requireContext())
        toolbox.saveDataString("InstiuteLogo" ,"${ImageUtil.BASE_URL_IMAGE}$InstituteLogo${imagePath}",requireContext())
    }

    fun fetchInstituteProfile(schoolId: String) {
        val apiService = RetrofitHelper.getApiService()
        val call = apiService.getInstituteProfile(schoolId)
        call.enqueue(object : Callback<InstituteProfileResponse> {
            override fun onResponse(
                call: Call<InstituteProfileResponse>,
                response: Response<InstituteProfileResponse>
            ) {
                if (response.isSuccessful) {
                    toolbox.stopLoadingBar()
                    //=============== Successfully received response===========
                    val instituteProfile = response.body()
                    if (instituteProfile != null) {
                        if(instituteProfile.data.website.isNullOrEmpty()){
                           binding.etWebsite.setText("Not Available")
                        }else{
//                            binding.etWebsite.setText(instituteProfile.website)
                        }

                       // Toast.makeText(requireContext(), instituteProfile.data.number, Toast.LENGTH_SHORT).show()

                        loadInstituteProfile(
                            instituteProfile.data.school_name,
                            instituteProfile.data.tag_line,
                            instituteProfile.data.number,
                            instituteProfile.data.website,
                            instituteProfile.data.institute_address,
                        )
                        toolbox.saveDataString("data.school_name", instituteProfile.data.school_name, requireContext())


                        loadInstituteImage(instituteProfile.data.institute_logo)
                    }
                } else {
                    toolbox.stopLoadingBar()
                    println("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<InstituteProfileResponse>, t: Throwable) {
                t.printStackTrace()
                println("API Call Failed: ${t.message}")
            }
        })
    }

//    private fun institutevalidation() {
//        val number = binding.etPhoneNumber.text.toString()
//
//        if (number.length == 10) {
//            val firstDigit = number.substring(0, 1)
//            if (firstDigit == "7" || firstDigit == "8" || firstDigit == "9") {
//                binding.layoutNumber.error = null
//                if ( Validation()){
//                    submitInstituteData()
//                }
//            } else {
//                binding.layoutNumber.error = "Phone number format invalid"
//            }
//        } else {
//            binding.layoutNumber.error = "Invalid phone number"
//        }
//    }

    private fun Validation(): Boolean {
        val fieldsToValidate = listOf(
            binding.etNameOfInstitute,
            binding.etTargetLine,
            binding.etPhoneNumber,
            binding.etWebsite,
            binding.etAddress,
            )

        if (binding.etPhoneNumber.text.toString().length == 10) {
            if(validation.validateTextInputFields(*fieldsToValidate.toTypedArray())){ return true }
        }
        return false
    }
}
