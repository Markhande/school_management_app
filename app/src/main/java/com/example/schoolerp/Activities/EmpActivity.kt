package com.example.schoolerp.Activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
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
import com.example.schoolerp.Fragments.EmpDashboard
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
import com.example.schoolerp.Fragments.MarksEmpAttendance
import com.example.schoolerp.Fragments.MarksStudentAttendance
import com.example.schoolerp.Fragments.NewClass
import com.example.schoolerp.Fragments.PaySalary
import com.example.schoolerp.Fragments.SalarySlip
import com.example.schoolerp.Fragments.SendMessageAdmin
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ActivityEmpBinding
import com.google.android.material.navigation.NavigationView

class EmpActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding:ActivityEmpBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var isLoweringLayingExpanded = false
    private var isClassExpanded = false
    private var isSubjectExpanded = false
    private var isStudentExpanded = false
    private var isEmployeeExpanded = false
    private var isAccountExpanded = false
    private var isFeesExpanded = false
    private var isSalaryExpanded = false
    private var isAttendanceExpanded = false
    private lateinit var menu: Menu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = findViewById(R.id.drawer_layoutemp)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup drawer toggle to sync the state of the drawer with the toolbar
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set default fragment (Dashboard) when the app is first launched
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EmpDashboard()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side_icons, menu)
        return true
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val id = menuItem.itemId

        // Switch to handle each menu item click event
        when (id) {
            R.id.nav_home -> {
                // Load the Dashboard fragment when "Home" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EmpDashboard()).commit()
            }
            R.id.nav_InstituteProfile -> {
                // Load the Institute fragment when "Institute Profile" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, InstituteProfile()).commit()
            }
            R.id.nav_FeeParticulars -> {
                // Load the FeeParticulars fragment when "Fee Particulars" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FeeParticular()).commit()
            }
//            R.id.nav_DetailsforFeesChallahan -> {
//                // Load the DetailsForFeesChallahan fragment when "Details for Fees Callahan" is clicked
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailsForFeesChallahan()).commit()
//            }
            R.id.nav_MarkingGrading -> {
                // Load the MarkingGrading fragment when "Marking Grading" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MarkingGrading()).commit()
            }
            R.id.nav_AccountSettings -> {
                // Load the AccountSettings fragment when "Account Settings" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    com.example.schoolerp.Fragments.Fragment.AccountSettings()
                ).commit()
            }
            R.id.nav_AllClasses -> {
                // Load the AllClasses fragment when "All Classes" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AllClass()).commit()
            }
            R.id.nav_NewClass -> {
                // Load the NewClass fragment when "New Class" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NewClass()).commit()
            }
            R.id.nav_ClassWithSubject -> {
                // Load the ClassWithSubject fragment when "Class With Subject" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ClassWithSubject()).commit()
            }
            R.id.nav_AssignSubject -> {
                // Load the AssignSubject fragment when "Assign Subject" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AssignSubject()).commit()
            }
            R.id.nav_AllStudents -> {
                // Load the AllStudents fragment when "All Students" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AllStudent()).commit()
            }
            R.id.nav_AddStudent -> {
                // Load the AddStudent fragment when "Add Student" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AddStudent()
                ).commit()

            }
            R.id.nav_AdmissionLetter -> {
                // Load the AdmissionLetter fragment when "Admission Letter" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AdmissionLetter()).commit()
            }
//            R.id.nav_PrintBasicList -> {
//                // Load the PrintBasicList fragment when "Print Basic List" is clicked
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PrintBasicList()).commit()
//            }
            R.id.nav_PromoteStudents -> {
                // Load the PromoteStudents fragment when "Promote Students" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PromoteStudents()).commit()
            }
            R.id.nav_StudentIDCards -> {
                // Load the StudentIdCard fragment when "Student ID Cards" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudentIdCard()).commit()
            }
            R.id.nav_AllEmployees -> {
                // Load the AllEmployees fragment when "All Employees" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AllEmployees()).commit()
            }
            R.id.nav_AddNewEmployees -> {
                // Load the AddNewEmployees fragment when "Add New Employees" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AddNewEmployees()
                ).commit()
            }
            R.id.nav_JobLetters -> {
                // Load the JobLetter fragment when "Job Letters" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, JobLetter()).commit()
            }
            R.id.nav_CharofAccount -> {
                // Load the ChartOfAccount fragment when "Chart of Account" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ChartOfAccount()).commit()
            }
            R.id.nav_AddIncome -> {
                // Load the AddIncome fragment when "Add Income" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AddIncome()
                ).commit()
            }
            R.id.nav_AddExpense -> {
                // Load the AddExpense fragment when "Add Expense" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AddExpense()
                ).commit()
            }
            R.id.nav_AccountStatement -> {
                // Load the AccountStatement fragment when "Account Statement" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    AccountStatement()
                ).commit()
            }
//            R.id.nav_GenerateBankCallahan -> {
//                // Load the GenerateBankCallahan fragment when "Generate Bank Callahan" is clicked
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GenerateBankChallahan()).commit()
//            }
            R.id.nav_Collectionfee -> {
                // Load the CollectionFee fragment when "Collection Fee" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CollectionFee()).commit()
            }
            R.id.nav_FeeSlip -> {
                // Load the FeeSlip fragment when "Fee Slip" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FeeSlip()).commit()
            }
            R.id.nav_FeesDefaulter -> {
                // Load the FeeDefaulter fragment when "Fees Defaulter" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FeeDefaulter()).commit()
            }
            R.id.nav_PaySalary -> {
                // Load the PaySalary fragment when "Pay Salary" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PaySalary()).commit()
            }
            R.id.nav_SalarySlip -> {
                // Load the SalarySlip fragment when "Salary Slip" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SalarySlip()).commit()
            }
            R.id.nav_MarksStudentAttendance -> {
                // Load the MarksStudentAttendance fragment when "Marks Student Attendance" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MarksStudentAttendance()).commit()
            }
            R.id.nav_MarksEmpAttendance -> {
                // Load the MarksStudentAttendance fragment when "Marks Student Attendance" is clicked
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MarksEmpAttendance()).commit()
            }

            R.id.nav_Homework -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeWork()).commit()
            }
            R.id.nav_Messages -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SendMessageAdmin()).commit()
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
                // Create an AlertDialog to confirm logout
                AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Do you really want to logout?")
                    .setPositiveButton("Yes") { dialog, which ->
//                        val intent = Intent(this, LoginPage::class.java)
//                        startActivity(intent)
//                       finish()
                       // logoutUser();
                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

                return true
            }
        }

        // Close the drawer after an item is selected
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun toggleGeneralSettingExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_InstituteProfile)
        val subitem2 = menu.findItem(R.id.nav_FeeParticulars)
//        val subitem3 = menu.findItem(R.id.nav_DetailsforFeesChallahan)
        val subitem4 = menu.findItem(R.id.nav_MarkingGrading)
        val subitem5 = menu.findItem(R.id.nav_AccountSettings)

        // Toggle visibility of each sub-item

        isLoweringLayingExpanded = !isLoweringLayingExpanded
        subitem1.isVisible = isLoweringLayingExpanded
        subitem2.isVisible = isLoweringLayingExpanded
//        subitem3.isVisible = isLoweringLayingExpanded
        subitem4.isVisible = isLoweringLayingExpanded
        subitem5.isVisible = isLoweringLayingExpanded

        // Refresh the navigation view to apply changes
        navigationView.invalidate()
    }

    private fun toggleClassExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_AllClasses)
        val subitem2 = menu.findItem(R.id.nav_NewClass)

        isClassExpanded = !isClassExpanded
        subitem1.isVisible = isClassExpanded
        subitem2.isVisible = isClassExpanded
    }

    private fun toggleSubjectExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_ClassWithSubject)
        val subitem2 = menu.findItem(R.id.nav_AssignSubject)

        isSubjectExpanded = !isSubjectExpanded
        subitem1.isVisible = isSubjectExpanded
        subitem2.isVisible = isSubjectExpanded
    }

    private fun toggleStudentExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_AllStudents)
        val subitem2 = menu.findItem(R.id.nav_AddStudent)
        val subitem3 = menu.findItem(R.id.nav_AdmissionLetter)
//        val subitem4 = menu.findItem(R.id.nav_PrintBasicList)
        val subitem5 = menu.findItem(R.id.nav_PromoteStudents)
        val subitem6 = menu.findItem(R.id.nav_StudentIDCards)

        isStudentExpanded = !isStudentExpanded
        subitem1.isVisible = isStudentExpanded
        subitem2.isVisible = isStudentExpanded
        subitem3.isVisible = isStudentExpanded
//        subitem4.isVisible = isStudentExpanded
        subitem5.isVisible = isStudentExpanded
        subitem6.isVisible = isStudentExpanded
    }

    private fun toggleEmployeeExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_AllEmployees)
        val subitem2 = menu.findItem(R.id.nav_AddNewEmployees)
        val subitem3 = menu.findItem(R.id.nav_JobLetters)

        isEmployeeExpanded = !isEmployeeExpanded
        subitem1.isVisible = isEmployeeExpanded
        subitem2.isVisible = isEmployeeExpanded
        subitem3.isVisible = isEmployeeExpanded
    }

    private fun toggleAccountExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_CharofAccount)
        val subitem2 = menu.findItem(R.id.nav_AddIncome)
        val subitem3 = menu.findItem(R.id.nav_AddExpense)
        val subitem4 = menu.findItem(R.id.nav_AccountStatement)

        isAccountExpanded = !isAccountExpanded
        subitem1.isVisible = isAccountExpanded
        subitem2.isVisible = isAccountExpanded
        subitem3.isVisible = isAccountExpanded
        subitem4.isVisible = isAccountExpanded
    }

    private fun toggleFeesExpandableItems() {
        menu = navigationView.menu
//        val subitem1 = menu.findItem(R.id.nav_GenerateBankCallahan)
        val subitem2 = menu.findItem(R.id.nav_Collectionfee)
        val subitem3 = menu.findItem(R.id.nav_FeeSlip)
        val subitem4 = menu.findItem(R.id.nav_FeesDefaulter)

        isFeesExpanded = !isFeesExpanded
//        subitem1.isVisible = isFeesExpanded
        subitem2.isVisible = isFeesExpanded
        subitem3.isVisible = isFeesExpanded
        subitem4.isVisible = isFeesExpanded
    }

    private fun toggleSalaryExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_PaySalary)
        val subitem2 = menu.findItem(R.id.nav_SalarySlip)

        isSalaryExpanded = !isSalaryExpanded
        subitem1.isVisible = isSalaryExpanded
        subitem2.isVisible = isSalaryExpanded
    }

    private fun toggleAttendanceExpandableItems() {
        menu = navigationView.menu
        val subitem1 = menu.findItem(R.id.nav_MarksStudentAttendance)
        val subitem2 = menu.findItem(R.id.nav_MarksEmpAttendance)

        isAttendanceExpanded = !isAttendanceExpanded
        subitem1.isVisible = isAttendanceExpanded
        subitem2.isVisible = isAttendanceExpanded
    }

    // Expand/collapse general settings sub-menu items

    override fun onBackPressed() {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            when {
                currentFragment is EmpDashboard -> {
                    showExitConfirmationDialog()
                }

                supportFragmentManager.backStackEntryCount > 0 -> {
                    supportFragmentManager.popBackStack()
                }

                else -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, EmpDashboard())
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
               // logoutUser()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}