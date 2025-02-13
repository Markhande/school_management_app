package com.example.schoolerp.Fragments

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.SearchStudentPrintAdapter
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentSearchStudentBinding
import com.example.schoolerp.util.ImageUtil


class SearchStudent : Fragment() {
    private lateinit var  binding:FragmentSearchStudentBinding
    private var isExpand = true
    val toolBox = MethodLibrary()
    private lateinit var studentsp: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding= FragmentSearchStudentBinding.bind(inflater.inflate(R.layout.fragment_search_student,null))
        arguments?.getParcelable<Student>("student")?.let { student ->
            populateFields(student)
        }
        binding.btnPrint.setOnClickListener {
            printSalarySlip()
        }

        return binding.root
    }

    companion object {
        const val REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 12345
    }

    // Handle the result from the screen lock authentication


    private fun populateFields(student: Student) {
        binding.etschoolId.setText(student.school_id)
        binding.etStudentName.setText(student.st_name)
        binding.etregistrationNo.setText(student.registration_no)
        binding.spStudentSelectClass.setText(student.class_name)
        binding.etDateOfAdmission.setText(student.dt_of_admission)
        binding.etDiscountFees.setText(student.discount_fee)
        binding.etPhoneNumber.setText(student.number)
        binding.etUsername.setText(student.username)
        binding.etPassword.setText(student.password)

        binding.etStatus.setText(student.st_status)

        if (student.st_status.equals("Inactive", ignoreCase = true)) {
            binding.etStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))}

        binding.edDateOfBirth.setText(student.dt_of_birth)
        binding.spSelectCast.setText(student.cast)
        binding.spSelectReligion.setText(student.religion)
        binding.spSelectBloodGroup.setText(student.blood_group)
        binding.etOrphanStudent.setText(student.orphan_student.toString())
        binding.etOSC.setText(student.osc)
        binding.etPreviousId.setText(student.previous_id)
        binding.etSelectFamily.setText(student.family)
        binding.edDiseaseIfAny.setText(student.disease_if_any)
        binding.etTotalSibling.setText(student.siblings)
        binding.etAddress.setText(student.address)
        binding.etFatherName.setText(student.father_name)
        binding.etFatherID.setText(student.father_id)
        binding.etFatherEducatino.setText(student.father_education)
        binding.etFatherMobile.setText(student.father_mobile)
        binding.etFatherOccupation.setText(student.father_occupation)
        binding.etFatherProfession.setText(student.father_profession)
        binding.etFatherIncome.setText(student.father_income)
        binding.etMotherName.setText(student.mother_name)
        binding.etMotherID.setText(student.mother_id)
        binding.etMotherEducation.setText(student.mother_education)
        binding.etMotherMobile.setText(student.mother_mobile)
        binding.etMotherOccupation.setText(student.mother_occupation)
        binding.etMotherProfession.setText(student.mother_profession)
        binding.etMotherIncome.setText(student.mother_income)
        binding.etIdentityMark.setText(student.identification_mark)
        binding.etPreviousSchool.setText(student.st_previous_school)
        binding.radioGroupGender.setText(student.gender)


        binding.studentPasswordUpdate.setOnClickListener {
            val data = mapOf(
                "student_id" to student.id,
                "role" to "admin",
            )
            toolBox.sendDataToFragment(requireContext(), StudentAccountSetting(), data)

        }

        toolBox.displayImage("${ImageUtil.BASE_URL_IMAGE}student/${student.picture}", binding.imageStudent, binding.root.context)

        studentsp = student.password


        // Variable to track password visibility state
        var isPasswordVisible = false

        // Initially hide the password
        binding.etPassword.text = "*".repeat(student.password.length)

        // Set up the listener for the visibility toggle button
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
                    // No screen lock is set, show the password immediately
                    binding.etPassword.text = student.password
                    binding.togglePasswordVisibility.setImageResource(R.drawable.eyeonpassword) // Change to "eye-open" icon
                }
            } else {
                // Hide the password
                binding.etPassword.text = "*".repeat(student.password.length)
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword)
            }
        }

        binding.txtSeeMore.setOnClickListener{
            isExpand = !isExpand
            MethodLibrary().toggleLayoutExpansion(
                AnimationLayout = binding.layoutstinfo,
                PrimaryText = binding.txtSeeMore,
                PrimaryTextMessage ="See More",
                SecondText = binding.txtSeeMore,
                SecondTextMessage = "See less",
                startingHeight = 0,
                EndingHeight = 860,
                Timing = 500L,
                DefaultValue = isExpand,
                requireContext()
            )
        }
    }

    private fun printSalarySlip() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val studentData = arguments?.getParcelable<Student>("student")

        val printAdapter = SearchStudentPrintAdapter(requireContext(), studentData)
        printManager.print("Student Information", printAdapter, PrintAttributes.Builder().build())
    }


    private fun getSpinnerIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(value, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == Activity.RESULT_OK) {
                binding.etPassword.text = studentsp
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeonpassword)

            } else {
                // Authentication failed or canceled, keep the password hidden
                binding.etPassword.text = "*".repeat(studentsp.length)
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword)

                Toast.makeText(context, "Authentication failed or canceled.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
