package com.example.schoolerp.Activities


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.Transition
import com.example.helpers.MethodLibrary
import com.example.onboardingschool.LoginPage
import com.example.schoolerp.Fragments.AccountStatement
import com.example.schoolerp.Fragments.AddExpense
import com.example.schoolerp.Fragments.AddIncome
import com.example.schoolerp.Fragments.AddNewEmployees
import com.example.schoolerp.Fragments.AllClass
import com.example.schoolerp.Fragments.AllEmployees
import com.example.schoolerp.Fragments.AllStudent
import com.example.schoolerp.Fragments.AssignSubject
import com.example.schoolerp.Fragments.ChartOfAccount
import com.example.schoolerp.Fragments.ClassWithSubject
import com.example.schoolerp.Fragments.CollectionFee
import com.example.schoolerp.Fragments.DashBoard
import com.example.schoolerp.Fragments.Fragment.AddStudent
import com.example.schoolerp.Fragments.Fragment.AdmissionLetter
import com.example.schoolerp.Fragments.Fragment.FeeDefaulter
import com.example.schoolerp.Fragments.Fragment.FeeParticular
import com.example.schoolerp.Fragments.FeeSlip
import com.example.schoolerp.Fragments.Fragment.InstituteProfile
import com.example.schoolerp.Fragments.Fragment.MarkingGrading
import com.example.schoolerp.Fragments.Fragment.PromoteStudents
import com.example.schoolerp.Fragments.Fragment.StudentIdCard
import com.example.schoolerp.Fragments.HomeWork
import com.example.schoolerp.Fragments.JobLetter
import com.example.schoolerp.Fragments.MarksStudentAttendance
import com.example.schoolerp.Fragments.NewClass
import com.example.schoolerp.Fragments.PaySalary
import com.example.schoolerp.Fragments.SalarySlip
import com.example.schoolerp.R
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.Fragments.Fragment.AccountSettings
import com.example.schoolerp.Fragments.MarksEmpAttendance
import com.example.schoolerp.Fragments.SearchStudent
import com.example.schoolerp.Fragments.SendMessageAdmin
import com.example.schoolerp.Fragments.StudentAccountSetting
import com.example.schoolerp.Fragments.TimeTable
import com.example.schoolerp.Fragments.TimeTableAdmin
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.ActivityMainBinding
import com.example.schoolerp.models.responses.LogoutResponse
import com.example.student.StudentBasicDetails
import com.example.student.StudentExamResult
import com.example.student.StudentFeeReceipt
import com.example.student.StudentHomework
import com.example.student.StudentMessaging
import com.example.student.StudentReportCard
import com.example.student.StudentTimetable
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.example.schoolerp.Fragments.EmployeeAttendanceReport
import com.example.schoolerp.Fragments.StudentAttendanceReport
import com.example.schoolerp.Fragments.calender
import com.example.schoolerp.models.responses.InstituteProfileResponse
import com.example.schoolerp.util.ImageUtil
import com.example.student.StudentInfo

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isLoweringLayingExpanded  = false
    private var isClassExpanded = false
    private var isSubjectExpanded = false
    val toolBox = MethodLibrary()
    private lateinit var instituteIcon: String
    private val InstituteLogo: String = "assetsNew/img/institute_logos/"
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       fetchInstituteProfile(SchoolId().getSchoolId(this))

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DashBoard()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        onPrepareOptionsMenu(navigationView.menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        when(SchoolId().getLoginRole(this)){
            "Student" -> studentItemShow(navigationView.menu)
            "Principal" -> principlaItemShow(navigationView.menu)
            "Teacher" ->  teacherItemShow(navigationView.menu)
            "Admin" ->  adminItemShow(navigationView.menu)
        }

        return true
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            super.onCreateOptionsMenu(menu)
            menuInflater.inflate(R.menu.main, menu)
            var rolLogin:String = SchoolId().getLoginRole(this)
            if (rolLogin.equals("Student") || rolLogin.equals("Teacher")) {
                menu?.findItem(R.id.action_sub_item_3_ms)?.isVisible = false
                menu?.findItem(R.id.action_sub_item_2_ms)?.isVisible = true
            }else if (rolLogin.equals("Admin") || rolLogin.equals("Principal") ){
                menu?.findItem(R.id.action_sub_item_3_ms)?.isVisible = true
                menu?.findItem(R.id.action_sub_item_2_ms)?.isVisible = true
            }
            toolBox.setIconItem(menu?.findItem(R.id.action_main_item_top),
                "${com.example.schoolerp.ImageUtil.ImageUtil.BASE_URL_IMAGE}$InstituteLogo${toolBox.getDataString("profileLogo" , this)}",this)
            return true
        }catch (e : Exception){
            toolBox.showConfirmationDialog(
                context = this,
                title = "Warning !",
                message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                positiveButtonText = "OK",
                positiveButtonAction = { toolBox.activityChanger(MainActivity(), this) },
                negativeButtonText = "",
                negativeButtonAction = { },
                cancelable = false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.action_main_item_top -> {
                true
            }
            R.id.action_sub_item_1_ms -> {
                toolBox.activityChanger(LoginPage(), this)
                true
            }
            R.id.action_sub_item_2_ms -> {

                toolBox.showConfirmationDialog(
                    context = this,
                    title = "Logout !",
                    message = "Are you sure you want to logout?",
                    positiveButtonText = "Logout",
                    positiveButtonAction = {
                        toolBox.clearSharedprepared("onboarding_prefs",this)
                        toolBox.clearSharedprepared("MyPrefs",this)
                        toolBox.activityChanger(LoginPage(), this) },
                    negativeButtonText = "Cancel",
                    negativeButtonAction = { },
                    cancelable = true )
                true
            }
            R.id.action_sub_item_3_ms -> {
                toolBox.fragmentChanger(AccountSettings(), this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun logoutUser() {
        RetrofitHelper.getApiService().logout().enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val logoutResponse = response.body()!!
                    if (logoutResponse.status) {
                        toolBox.clearSharedprepared("onboarding_prefs",this@MainActivity)
                        toolBox.clearSharedprepared("MyPrefs",this@MainActivity)
                        toolBox.activityChanger(LoginPage(), this@MainActivity)
                    } else {
                        toolBox.showConfirmationDialog(
                            context = this@MainActivity,
                            title = "Warning !",
                            message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                            positiveButtonText = "OK",
                            positiveButtonAction = { toolBox.activityChanger(MainActivity(),this@MainActivity) },
                            negativeButtonText = "",
                            negativeButtonAction = { },
                            cancelable = false
                        )
                    }
                } else {
                    toolBox.showConfirmationDialog(
                        context = this@MainActivity,
                        title = "Warning !",
                        message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                        positiveButtonText = "OK",
                        positiveButtonAction = { toolBox.activityChanger(MainActivity(),this@MainActivity) },
                        negativeButtonText = "",
                        negativeButtonAction = { },
                        cancelable = false
                    )
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Logout failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Handles item selections in the navigation drawer
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val id = menuItem.itemId

        when (id) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DashBoard()).commit()
            }
            R.id.nav_InstituteProfile -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, InstituteProfile()).commit()
            }
            R.id.nav_FeeParticulars -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FeeParticular()).commit()
            }
//            R.id.nav_DetailsforFeesChallahan -> {
//                // Load the DetailsForFeesChallahan fragment when "Details for Fees Callahan" is clicked
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailsForFeesChallahan()).commit()
//            }
//            R.id.nav_MarkingGrading -> {
//                // Load the MarkingGrading fragment when "Marking Grading" is clicked
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MarkingGrading()).commit()
//            }
            R.id.nav_AccountSettings -> {
                // Load the AccountSettings fragment when "Account Settings" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    com.example.schoolerp.Fragments.Fragment.AccountSettings()
                ).commit()
            }
            R.id.nav_Event -> {
                // Load the MarkingGrading fragment when "Marking Grading" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, calender()).commit()
            }
            R.id.nav_AllClasses -> {
                // Load the AllClasses fragment when "All Classes" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AllClass()).commit()
            }
            R.id.nav_NewClass -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NewClass()).commit()
            }
            R.id.nav_ClassWithSubject -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ClassWithSubject()).commit()
            }
            R.id.nav_AssignSubject -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AssignSubject()).commit()
            }
            R.id.nav_AllStudents -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AllStudent()).commit()
            }
            R.id.nav_AddStudent -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AddStudent()
                ).commit()

            }
            R.id.nav_AdmissionLetter -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AdmissionLetter()).commit()
            }
//            R.id.nav_PrintBasicList -> {
//                // Load the PrintBasicList fragment when "Print Basic List" is clicked
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PrintBasicList()).commit()
//            }
            R.id.nav_PromoteStudents -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PromoteStudents()).commit()
            }
            R.id.nav_StudentIDCards -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentIdCard()).commit()
            }
            R.id.nav_AllEmployees -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AllEmployees()).commit()
            }

            R.id.nav_AddNewEmployees -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AddNewEmployees()
                ).commit()
            }
            R.id.nav_JobLetters -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, JobLetter()).commit()
            }
            R.id.nav_CharofAccount -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ChartOfAccount()).commit()
            }
            R.id.nav_AddIncome -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AddIncome()
                ).commit()
            }
            R.id.nav_AddExpense -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AddExpense()
                ).commit()
            }
            R.id.nav_AccountStatement -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AccountStatement()
                ).commit()
            }
//            R.id.nav_GenerateBankCallahan -> {
//                // Load the GenerateBankCallahan fragment when "Generate Bank Callahan" is clicked
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GenerateBankChallahan()).commit()
//            }
            R.id.nav_Collectionfee -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CollectionFee()).commit()
            }
            R.id.nav_FeeSlip -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FeeSlip()).commit()
            }
            R.id.nav_FeesDefaulter -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FeeDefaulter()).commit()
            }
            R.id.nav_PaySalary -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PaySalary()).commit()
            }
            R.id.nav_SalarySlip -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SalarySlip()).commit()
            }
            R.id.nav_MarksStudentAttendance -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MarksStudentAttendance()).commit()
            }

            R.id.nav_StudentAttendanceReport -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentAttendanceReport()).commit()
            }

            R.id.nav_MarksEmpAttendance -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MarksEmpAttendance()).commit()
            }

            R.id.nav_EmployeeAttendanceReport -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EmployeeAttendanceReport()).commit()
            }

            R.id.nav_Homework -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeWork()).commit()
            }
            R.id.nav_Messages -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SendMessageAdmin()).commit()
            }

            R.id.nav_student_details -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentBasicDetails()).commit()
            }

            R.id.nav_student_Paid_fee_recipt -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentFeeReceipt()).commit()
            }

            R.id.nav_student_my_time_table -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentTimetable()).commit()
            }


            R.id.nav_TimeTable -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, TimeTableAdmin()).commit()
            }

            R.id.nav_student_home_work -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentHomework()).commit()
            }

            R.id.nav_student_message -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentMessaging()).commit()
            }

            R.id.nav_student_account_settings -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentAccountSetting()).commit()
            }

            R.id.nav_Gsettings -> {
                toggleGeneralSettingExpandableItems()
                return true
            }
            // Class section
            R.id.nav_Class -> {
                toggleClassExpandableItems() // Expand/collapse class items
                return true
            }
            // Subject section
            R.id.nav_Subject -> {
                toggleSubjectExpandableItems() // Expand/collapse subject items
                return true
            }
            // Student section
            R.id.nav_Student -> {
                toggleStudentExpandableItems() // Expand/collapse student items
                return true
            }
            // Employee section
            R.id.nav_Employee -> {
                toggleEmployeeExpandableItems() // Expand/collapse employee items
                return true
            }
            // Accounts section
            R.id.nav_Accounts -> {
                toggleAccountExpandableItems() // Expand/collapse accounts items
                return true
            }
            // Fees section
            R.id.nav_Fees -> {
                toggleFeesExpandableItems() // Expand/collapse fees items
                return true
            }
            // Salary section
            R.id.nav_salary -> {
                toggleSalaryExpandableItems() // Expand/collapse salary items
                return true
            }
            // Attendance section
            R.id.nav_Attendance -> {
                toggleAttendanceExpandableItems() // Expand/collapse attendance items
                return true
            }

            R.id.nav_logout -> {
                toolBox.showConfirmationDialog(
                    context = this@MainActivity,
                    title = "Logout !",
                    message = "Are you sure you want to logout?",
                    positiveButtonText = "Logout",
                    positiveButtonAction = {
                        logoutUser()
                                           },
                    negativeButtonText = "Cancel",
                    negativeButtonAction = { },
                    cancelable = true )

                return true
            }

        }

        // Close the drawer after an item is selected
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Expand/collapse general settings sub-menu items
    private fun toggleGeneralSettingExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_InstituteProfile)
        val subitem2 = menu.findItem(R.id.nav_FeeParticulars)
//        val subitem3 = menu.findItem(R.id.nav_DetailsforFeesChallahan)
//        val subitem4 = menu.findItem(R.id.nav_MarkingGrading)
        val subitem5 = menu.findItem(R.id.nav_AccountSettings)
        val subitem6 = menu.findItem(R.id.nav_Event)

        // Toggle visibility of each sub-item

        isLoweringLayingExpanded = !isLoweringLayingExpanded
        subitem1.isVisible = isLoweringLayingExpanded
        subitem2.isVisible = isLoweringLayingExpanded
//        subitem3.isVisible = isLoweringLayingExpanded
//        subitem4.isVisible = isLoweringLayingExpanded
        subitem5.isVisible = isLoweringLayingExpanded
        subitem6.isVisible = isLoweringLayingExpanded

        // Refresh the navigation view to apply changes
        navigationView.invalidate()
    }

    // Helper function to toggle visibility for any set of menu items
    private fun toggleExpandableItems(items: List<Int>, roleBasedItems: Map<String, List<Int>>? = null) {
        menu = navigationView.menu
        isClassExpanded = !isClassExpanded

        // Toggle visibility for the provided list of item IDs
        for (itemId in items) {
            val item = menu.findItem(itemId)
            item.isVisible = isClassExpanded
        }

        // Handle role-based items (e.g., "Student" vs. "Admin")
        roleBasedItems?.forEach { (role, itemIds) ->
            if (SchoolId().getLoginRole(this).equals(role, ignoreCase = true)) {
                for (itemId in itemIds) {
                    val item = menu.findItem(itemId)
                    item.isVisible = isClassExpanded
                }
            }
        }
    }

// Toggle methods for specific categories:

    private fun toggleClassExpandableItems() {
        toggleExpandableItems(
            items = listOf(R.id.nav_AllClasses, R.id.nav_NewClass)
        )
    }

    private fun toggleSubjectExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_ClassWithSubject)
        val subitem2 = menu.findItem(R.id.nav_AssignSubject)

        isSubjectExpanded = !isSubjectExpanded

        val roleVisibilityMap = mapOf(
            "Student" to listOf(subitem1),
            "Admin" to listOf(subitem1, subitem2),
            "Principal" to listOf(subitem1, subitem2)

        )

        roleVisibilityMap[SchoolId().getLoginRole(this)]?.forEach {
            it.isVisible = isSubjectExpanded
        }
    }


    private fun toggleStudentExpandableItems() {
        toggleExpandableItems(
            items = listOf(
                R.id.nav_AllStudents,
                R.id.nav_AddStudent,
                R.id.nav_PromoteStudents,
//                R.id.nav_StudentIDCards
            )
        )
    }

    private fun toggleEmployeeExpandableItems() {
        toggleExpandableItems(
            items = listOf(
                R.id.nav_AllEmployees, R.id.nav_AddNewEmployees, R.id.nav_JobLetters)
        )
    }

    private fun toggleAccountExpandableItems() {
        toggleExpandableItems(
            items = listOf(
                R.id.nav_CharofAccount, R.id.nav_AddIncome, R.id.nav_AddExpense, R.id.nav_AccountStatement
            )
        )
    }

    private fun toggleFeesExpandableItems() {
        toggleExpandableItems(
            items = listOf(R.id.nav_Collectionfee, R.id.nav_FeeSlip, R.id.nav_FeesDefaulter)
        )
    }

    private fun toggleSalaryExpandableItems() {
        toggleExpandableItems(
            items = listOf(R.id.nav_PaySalary, R.id.nav_SalarySlip)
        )
    }



    private fun toggleAttendanceExpandableItems() {
        if (SchoolId().getLoginRole(this).equals("Teacher"))
        toggleExpandableItems(items = listOf(R.id.nav_MarksStudentAttendance,R.id.nav_StudentAttendanceReport))
        else{
            toggleExpandableItems(items = listOf(R.id.nav_MarksStudentAttendance, R.id.nav_MarksEmpAttendance,R.id.nav_StudentAttendanceReport,R.id.nav_EmployeeAttendanceReport))
        }
    }


    // Override back button to close the drawer if it's open, otherwise perform the default action
    /*  override fun onBackPressed() {
          if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
              drawerLayout.closeDrawer(GravityCompat.START)
          } else {
              super.onBackPressed()
          }
      }*/

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // Check if the navigation drawer is open, and if so, close it
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Handle back press depending on the current fragment
            when {
                currentFragment is DashBoard -> {
                    // If it's the DashBoard fragment, show exit confirmation
                    showExitConfirmationDialog()
                }

                supportFragmentManager.backStackEntryCount > 0 -> {
                    // If there are fragments in the back stack, pop the top one
                    supportFragmentManager.popBackStack()
                }

                else -> {
                    // If no fragments are in the back stack, replace the fragment with DashBoard
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, DashBoard())
                        .commit()
                }
            }
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to exit the app?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                // Exit the app
                finish()  // Close the current activity (exit the app)
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog without exiting
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun studentItemShow(menu: Menu?) {
        fun setMenuItemVisibility(itemId: Int, isVisible: Boolean) {
            menu?.findItem(itemId)?.isVisible = isVisible
        }
        setMenuItemVisibility(R.id.nav_student_Paid_fee_recipt, true)
        setMenuItemVisibility(R.id.nav_student_my_time_table, true)
        setMenuItemVisibility(R.id.nav_student_home_work, true)
        setMenuItemVisibility(R.id.nav_student_message, true)
        setMenuItemVisibility(R.id.nav_student_account_settings, false)
        setMenuItemVisibility(R.id.nav_student_details, true)
    }

    private fun teacherItemShow(menu: Menu?){
        fun setMenuItemVisibility(itemId: Int, isVisible: Boolean) {
            menu?.findItem(itemId)?.isVisible = isVisible
        }
        setMenuItemVisibility(R.id.nav_home, true)
        setMenuItemVisibility(R.id.nav_Attendance, true)
        setMenuItemVisibility(R.id.nav_Homework, true)
        setMenuItemVisibility(R.id.nav_Messages, true)
        setMenuItemVisibility(R.id.nav_TimeTable, true)

    }

    private fun adminItemShow(menu: Menu?){
        fun setMenuItemVisibility(itemId: Int, isVisible: Boolean) {
            menu?.findItem(itemId)?.isVisible = isVisible
        }

        setMenuItemVisibility(R.id.nav_Employee, true)
        setMenuItemVisibility(R.id.nav_home, true)
        setMenuItemVisibility(R.id.nav_Gsettings, true)
        setMenuItemVisibility(R.id.nav_Fees, true)
        setMenuItemVisibility(R.id.nav_salary, true)
        setMenuItemVisibility(R.id.nav_Attendance, true)
        setMenuItemVisibility(R.id.nav_Homework, true)
        setMenuItemVisibility(R.id.nav_Messages, true)
        setMenuItemVisibility(R.id.nav_Accounts, true)
        setMenuItemVisibility(R.id.nav_Student, true)
        setMenuItemVisibility(R.id.nav_Class, true)
        setMenuItemVisibility(R.id.nav_Subject, true)
        setMenuItemVisibility(R.id.nav_TimeTable, true)

    }

    private fun principlaItemShow(menu: Menu?){
        fun setMenuItemVisibility(itemId: Int, isVisible: Boolean) {
            menu?.findItem(itemId)?.isVisible = isVisible
        }

        setMenuItemVisibility(R.id.nav_Employee, true)
        setMenuItemVisibility(R.id.nav_home, true)
        setMenuItemVisibility(R.id.nav_Gsettings, true)
        setMenuItemVisibility(R.id.nav_Fees, true)
        setMenuItemVisibility(R.id.nav_salary, true)
        setMenuItemVisibility(R.id.nav_Attendance, true)
        setMenuItemVisibility(R.id.nav_Homework, true)
        setMenuItemVisibility(R.id.nav_Messages, true)
        setMenuItemVisibility(R.id.nav_Accounts, true)
        setMenuItemVisibility(R.id.nav_Student, true)
        setMenuItemVisibility(R.id.nav_Class, true)
        setMenuItemVisibility(R.id.nav_Subject, true)
        setMenuItemVisibility(R.id.nav_TimeTable, true)

    }
//
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
                            toolBox.saveDataString("profileLogo", instituteProfile.data.institute_logo, this@MainActivity)
                            toolBox.saveDataString("institute_address", instituteProfile.data.institute_address, this@MainActivity)
                            toolBox.saveDataString("school_name", instituteProfile.data.school_name, this@MainActivity)


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
            toolBox.showConfirmationDialog(
                context = this,
                title = "Warning !",
                message = "Unknown Error found Please Contact with developer nurkude@gmail.com",
                positiveButtonText = "OK",
                positiveButtonAction = { toolBox.activityChanger(MainActivity(), this) },
                negativeButtonText = "",
                negativeButtonAction = { },
                cancelable = false)
        }
    }

}