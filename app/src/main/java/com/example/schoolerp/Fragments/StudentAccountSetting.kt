package com.example.schoolerp.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.helpers.MethodLibrary
import com.example.helpers.validation
import com.example.onboardingschool.LoginPage
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentStudentAccountSettingBinding
import com.example.schoolerp.viewmodel.StudentPasswordUpdateViewModel
import com.example.schoolerp.viewmodelfactory.StudentPasswordUpdateViewModelFactory
import com.example.student.StudentInfo


class StudentAccountSetting : Fragment() {

    private lateinit var binding: FragmentStudentAccountSettingBinding
    private lateinit var studentPasswordUpdateViewModel: StudentPasswordUpdateViewModel
    val toolBox = MethodLibrary()
    private lateinit var student_id:String
    private lateinit var role:String
    var isExpand = false
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentAccountSettingBinding.inflate(inflater, container, false)
        student_id = StudentInfo().getStudentId(requireContext())

        val arguments = arguments
            if (arguments != null) {
                if (SchoolId().getLoginRole(requireContext()).equals("Admin")){
                    student_id = arguments.getString("student_id").toString()
                }else{
                    student_id = StudentInfo().getStudentId(requireContext())
                }
            }



        val factory = StudentPasswordUpdateViewModelFactory(requireActivity().application)
        studentPasswordUpdateViewModel = ViewModelProvider(this, factory).get(StudentPasswordUpdateViewModel::class.java)

        setTextDetail()

        binding.btnUpdateAccountSetting.setOnClickListener {
            if (binding.studentNewPassword.text.toString().isNotEmpty()){
                if (binding.studentNewPassword.text.toString().length >= 6){
                    val studentDetails = mapOf(
                        "id" to student_id,
                        "school_id" to SchoolId().getSchoolId(requireContext()),
                        "password" to binding.studentNewPassword.text.toString())
                    studentPasswordUpdateViewModel.updatePassword(studentDetails)
                }else{
                    validation().errorLayout(binding.updateLayoutStudent , "password must be above 6 character")
                }
            }else{
                validation().errorLayout(binding.updateLayoutStudent , "Please enter new password")
                //Toast.makeText(requireContext(), "Please enter new password", Toast.LENGTH_SHORT).show()
            }
        }

        studentPasswordUpdateViewModel.updatePasswordResponse.observe(viewLifecycleOwner, Observer { response ->
            // Handle response
            if (SchoolId().getLoginRole(requireContext()).equals("Admin")){
                if (response != null) {
                    toolBox.showConfirmationDialog(
                        context = requireContext(),
                        title = "Password has changed successfully",
                        positiveButtonText = "OK",
                        positiveButtonAction = { toolBox.fragmentChanger(AllStudent(),requireContext()) },
                        cancelable = true
                    )
                }
            }else{
                if (response != null) {
                    toolBox.clearSharedprepared("onboarding_prefs", requireActivity())
                    toolBox.showConfirmationDialog(
                        context = requireContext(),
                        title = "Password has changed successfully",
                        message = "You have to login again with updated password",
                        positiveButtonText = "Login",
                        positiveButtonAction = { toolBox.activityChanger(LoginPage(), requireContext()) },
                        cancelable = false
                    )
                }
            }


        })


        return binding.root
    }
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null!!
//    }

    private fun setTextDetail() {
        binding.etusername.setText(SchoolId().getUsername(requireContext()))
        binding.edtusername.setText(SchoolId().getUsername(requireContext()))
        binding.etpassword.setText(SchoolId().getPassword(requireContext()))
    }


}
