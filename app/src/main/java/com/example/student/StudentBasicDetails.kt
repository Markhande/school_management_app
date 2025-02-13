package com.example.student

import android.animation.ValueAnimator
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.helpers.MethodLibrary
import com.example.onboardingschool.LoginPage
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.Class
import com.example.schoolerp.Fragments.SearchStudent.Companion.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS
import com.example.schoolerp.Fragments.StudentAccountSetting
import com.example.schoolerp.ImageUtil.ImageUtil
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.ActivityMainBinding
import com.example.schoolerp.databinding.FragmentStudentAccountSettingBinding
import com.example.schoolerp.databinding.FragmentStudentBasicDetailsBinding
import com.example.schoolerp.databinding.FragmentStudentTimetableBinding
import java.util.Objects

class StudentBasicDetails : Fragment() {
    private lateinit var binding: FragmentStudentBasicDetailsBinding
    private var StudentImage: String = "assetsNew/img/student/"
    var  stdetails = StudentInfo()
    var isExpanded: Boolean = false
    val toolbox = MethodLibrary()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentBasicDetailsBinding.bind(inflater.inflate(R.layout.fragment_student_basic_details, null))

//        setLayoutHeight(550)
        binding.txtSeeMore.setOnClickListener {
//            showMoreDetails()
//            showLessDetails()
        }

        binding.txtSeeMore.setOnClickListener {
            showLessDetails()
        }

        binding.imgUpdatePassword.setOnClickListener {
            toolbox.fragmentChanger(StudentAccountSetting(), requireContext())
        }

        var isPasswordVisible = false

       binding.etpassword.text = "*".repeat(SchoolId().getPassword(requireContext()).length)


        binding.togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                val keyguardManager = requireContext().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

                if (keyguardManager.isKeyguardSecure) {
                    val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                        "Authenticate",
                        "Please enter your device password to view the password"
                    )
                    startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS)
                } else {

                    binding.etpassword.text = SchoolId().getPassword(requireContext())
                    binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword) // Change to "eye-open" icon
                }
            } else {
                // Hide the password
                binding.etpassword.text = "*".repeat(SchoolId().getPassword(requireContext()).length)
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeonpassword)
            }
        }
        setTextDetail()
        return binding.root


    }

    private fun setTextDetail(){
        binding.etStudentName.setText(stdetails.getStudentName(requireContext()))
        binding.etregistrationNo.setText(stdetails.getStudentRegistrationId(requireContext()))
        binding.etschoolId.setText(SchoolId().getSchoolId(requireContext()))
        binding.studentClass.setText(stdetails.getStudentClass(requireContext()))
        binding.etDateOfAdmission.setText(stdetails.getStudentDtofAdd(requireContext()))
        binding.etusername.setText(SchoolId().getUsername(requireContext()))
//        binding.etpassword.setText(SchoolId().getPassword(requireContext()))
        binding.etStatus.setText(stdetails.getStudentStatus(requireContext()))
        binding.edDateOfBirth.setText(stdetails.getStudentDtofBirth(requireContext()))
        binding.etDiscountFees.setText(stdetails.getStudentDiscountFee(requireContext()))
        binding.etPhoneNumber.setText(stdetails.getStudentPhoneNumber(requireContext()))
        binding.spSelectReligion.setText(stdetails.getStudentReligion(requireContext()))
        binding.spSelectCast.setText(stdetails.getStudentCaste(requireContext()))
        binding.radioGroupGender.setText(stdetails.getStudentGender(requireContext()))
        binding.spSelectBloodGroup.setText(stdetails.getStudentBloodGroup(requireContext()))
        binding.edDiseaseIfAny.setText(stdetails.getStudentDisease(requireContext()))
        binding.etSelectFamily.setText(stdetails.getStudentFamily(requireContext()))
        binding.etTotalSibling.setText(stdetails.getStudentSiblings(requireContext()))
        binding.etOrphanStudent.setText(stdetails.getOrphanStudent(requireContext()))
        binding.etOSC.setText(stdetails.getStudentOsc(requireContext()))
        binding.etPreviousSchool.setText(stdetails.getStudentPreviousSchool(requireContext()))
        binding.etPreviousId.setText(stdetails.getStudentPreviousId(requireContext()))
        binding.etIdentityMark.setText(stdetails.getstudentIdentificationMark(requireContext()))
        binding.etAddress.setText(stdetails.getStudentAddress(requireContext()))
        binding.etFatherName.setText(stdetails.getStudentFatherName(requireContext()))
        binding.etFatherEducatino.setText(stdetails.getFatherEducation(requireContext()))
        binding.etFatherID.setText(stdetails.getStudentFatherId(requireContext()))
        binding.etFatherMobile.setText(stdetails.getFatherMobile(requireContext()))
        binding.etFatherProfession.setText(stdetails.getFatherProfession(requireContext()))
        binding.etFatherOccupation.setText(stdetails.getFatherOccupation(requireContext()))
        binding.etFatherIncome.setText(stdetails.getFatherIncome(requireContext()))
        binding.etMotherName.setText(stdetails.getStudentMotherName(requireContext()))
        binding.etMotherEducation.setText(stdetails.getMotherEducation(requireContext()))
        binding.etMotherID.setText(stdetails.getStudentMotherId(requireContext()))
        binding.etMotherMobile.setText(stdetails.getMotherMobile(requireContext()))
        binding.etMotherOccupation.setText(stdetails.getMotherOccupation(requireContext()))
        binding.etMotherProfession.setText(stdetails.getMotherProfession(requireContext()))
        binding.etMotherIncome.setText(stdetails.getMotherIncome(requireContext()))

        //loadStudentImage()
        toolbox.displayImage("${ImageUtil.BASE_URL_IMAGE}$StudentImage${stdetails.getStudentPicture(requireContext())}",binding.imgstudent, requireContext())
    }


    private fun showLessDetails(){
        val startHeight = binding.animatedLayout.height
        val targetHeight: Int

        if (isExpanded) {
            targetHeight = (resources.displayMetrics.density * 265).toInt()
            binding.txtSeeMore.text = "See More..."
        } else {
        //=========== Expand to the new height (620dp)===============
            targetHeight = (resources.displayMetrics.density * 1418).toInt()
            binding.txtSeeMore.text = "See Less"
        }

        val animator = ValueAnimator.ofInt(startHeight, targetHeight)
        animator.duration = 500

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val params = binding.animatedLayout.layoutParams
            params.height = value
            binding.animatedLayout.layoutParams = params
        }
        animator.start()
        isExpanded = !isExpanded
    }

    private fun setLayoutHeight(height: Int) {
        val linearLayout = binding.animatedLayout
        val layoutParams = linearLayout.layoutParams
        layoutParams.height = height
        linearLayout.layoutParams = layoutParams
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == Activity.RESULT_OK) {
                binding.etpassword.text = SchoolId().getPassword(requireContext())
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword)

            } else {
                // Authentication failed or canceled, keep the password hidden
                binding.etpassword.text = "*".repeat(SchoolId().getPassword(requireContext()).length)
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeonpassword)

                Toast.makeText(context, "Authentication failed or canceled.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}







