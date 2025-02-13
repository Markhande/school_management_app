package com.example.schoolerp.Fragments

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Adapter.SalarySlipPrintAdapter
import com.example.schoolerp.Adapter.SearchEmpPrintAdapter
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.DataClasses.SalarySlipData
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.Fragments.SearchStudent.Companion.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentSearchEmpBinding
import com.example.schoolerp.util.ImageUtil
import com.example.teacher.TeacherDetails

class SearchEmp : Fragment() {
    private lateinit var binding: FragmentSearchEmpBinding
    var toolbox= MethodLibrary()
    private var isExpand = true
    val toolBox = MethodLibrary()
    private var EmployeeImage:String="assetsNew/img/student/"
    var  teacherDetails = TeacherDetails()
    private lateinit var empPass: String
    private lateinit var profilePic :  String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentSearchEmpBinding.bind(inflater.inflate(R.layout.fragment_search_emp, null))
        arguments?.getParcelable<AllEmployee>("employee")?.let { emp ->
            populateFields(emp)
        }



        binding.btnPrint.setOnClickListener {
            printSalarySlip()
        }
        return binding.root
    }
    private fun populateFields(emp: AllEmployee){
        binding.edtexperience.setText(emp.experience)
        binding.etStudentName.setText(emp.employee_name)
        //  binding.etregistrationNo.setText(emp.re)
        binding.spStudentSelectClass.setText(emp.employee_role)
        binding.etDateOfAdmission.setText(emp.date_of_joining)
        //   binding.etDiscountFees.setText(emp.discount_fee)
        //  binding.etPhoneNumber.setText(emp.)
        binding.etUsername.setText(emp.username)
        binding.etPassword.setText(emp.password)
        binding.etStatus.setText(emp.st_status)

        if (emp.st_status.equals("Inactive", ignoreCase = true)) {
            binding.etStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))}

        binding.edtemailaddres.setText(emp.email)
        binding.etEducation.setText(emp.education)
        binding.edAdhaarPanNo.setText(emp.national_id)
        binding.etPhoneNumber.setText(emp.number)
        binding.edDateOfBirth.setText(emp.date_of_birth)
        binding.spSelectReligion.setText(emp.religion)
        binding.spSelectBloodGroup.setText(emp.blood_group)
        binding.etAddress.setText(emp.home_address)
        binding.etFatherName.setText(emp.f_h_name)
        binding.etSalary.setText(emp.monthly_salary)
        binding.radioGroupGender.setText(emp.gender)
        profilePic = emp.picture.toString()

        MethodLibrary().displayImage("${ImageUtil.BASE_URL_IMAGE}employee/${emp.picture}", binding.imgEmp , binding.root.context)

        empPass = emp.password



        var isPasswordVisible = false

        binding.etPassword.text = "*".repeat(empPass.length)

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
                }else{
                    binding.etPassword.text = empPass
                    binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword)
                }
            } else {
                // Hide the password
                binding.etPassword.text = "*".repeat(empPass.length)
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeonpassword) // Change to "eye-closed" icon
            }

        }

        binding.txtSeeMore.setOnClickListener{
            isExpand = !isExpand
            MethodLibrary().toggleLayoutExpansion(
                AnimationLayout = binding.layoutEmpInfo,
                PrimaryText = binding.txtSeeMore,
                PrimaryTextMessage ="See More",
                SecondText = binding.txtSeeMore,
                SecondTextMessage = "See less",
                startingHeight = 0,
                EndingHeight = 380,
                Timing = 500L,
                DefaultValue = isExpand,
                requireContext()
            )
        }



        /*  binding.txtSeeMore.setOnClickListener {
              binding.layoutStInfo.visibility =
                  if (binding.layoutStInfo.visibility == View.GONE) {
                      binding.txtSeeMore.text = "See Less"
                      View.VISIBLE

                  } else {
                      binding.txtSeeMore.text
                      View.GONE
                  }*/
    }
    private fun printSalarySlip() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val empdata = arguments?.getParcelable<AllEmployee>("employee")
        val printAdapter = SearchEmpPrintAdapter(requireContext(), empdata)
        printManager.print("Employees Information", printAdapter, PrintAttributes.Builder().build())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == Activity.RESULT_OK) {
                binding.etPassword.text = empPass
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeoffpassword)

            } else {
                // Authentication failed or canceled, keep the password hidden
                binding.etPassword.text = "*".repeat(empPass.length)
                binding.togglePasswordVisibility.setImageResource(R.drawable.eyeonpassword)

                Toast.makeText(context, "Authentication failed or canceled.", Toast.LENGTH_SHORT)
                    .show()
            }
            }
        }
    }


