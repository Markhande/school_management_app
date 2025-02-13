package com.example.schoolerp.Fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.Api.RetrofitHelper.getApiService
import com.example.schoolerp.databinding.FragmentAssignSubjectBinding
import com.example.schoolerp.models.responses.AllClassNameResponse
import com.example.schoolerp.repository.AllClassRepository
import com.example.schoolerp.repository.SubjectRepository
import com.example.schoolerp.viewmodel.AllClassViewModel
import com.example.schoolerp.viewmodel.SubjectViewModel
import com.example.schoolerp.viewmodelfactory.AllClassViewModelFactory
import com.example.schoolerp.viewmodelfactory.SubjectViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignSubject : Fragment() {
    private lateinit var binding: FragmentAssignSubjectBinding
    private lateinit var viewModel: SubjectViewModel
    private lateinit var viewModelAllClassAssignSubejct: AllClassViewModel
    private var layoutCount = 0
    var toolBox = MethodLibrary()
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignSubjectBinding.inflate(inflater, container, false)
        if (!toolBox.isInternetAvailable(requireContext())){
            toolBox.deviceOffLineMassage(requireContext())
        }else{
            toolBox.startLoadingBar("Loading...",false, requireContext())
            // ViewModel setup
            val apiService = getApiService()
            val repository = SubjectRepository(apiService)
            val factory = SubjectViewModelFactory(repository)
            viewModel = ViewModelProvider(this, factory).get(SubjectViewModel::class.java)

            initObservers()
            setupListeners()
//      fetchClassNames(SchoolId().getSchoolId(requireContext()))
            toolBox.clicked(binding.tvAddNewClassbtn){toolBox.fragmentChanger(NewClass(), requireContext())}
            viewModelAllClass()
            setupAllClassObserver()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        // Add more layouts
        binding.ltAddMore.setOnClickListener {
            if (layoutCount < 9) { // Maximum of 10 layouts
                toggleLayoutVisibility(layoutCount, true)
                layoutCount++
            } else {
                Toast.makeText(context, "All layouts are already visible", Toast.LENGTH_SHORT).show()
            }
        }

        // Remove layouts
        binding.ltRemove.setOnClickListener {
            if (layoutCount > 0) { // Minimum of 1 layout
                layoutCount--
                toggleLayoutVisibility(layoutCount, false)
               // Toast.makeText(context, "Layout removed", Toast.LENGTH_SHORT).show()
            } else {
               // Toast.makeText(context, "No more layouts to remove", Toast.LENGTH_SHORT).show()
            }
        }

        // Collect and submit data
        binding.btnAssignSubject.setOnClickListener {
            if (!toolBox.isInternetAvailable(requireContext())){
                toolBox.deviceOffLineMassage(requireContext())
            }else{
                if (!binding.SelectClass.selectedItem.toString().equals("Select Class")){
                    val dataList = collectDataFromLayouts()
                    if (dataList.isNotEmpty()) {
                        viewModel.addMultipleSubjects(dataList)
                    } else {
                        vibrator?.vibrate(
                            VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 300), -1) // Pattern: Vibrate, pause, vibrate
                        )
                        Toast.makeText(context, "Please fill in all fields before submitting.", Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(context, "Please Select Class", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Helper to toggle visibility of layouts
    private fun toggleLayoutVisibility(index: Int, isVisible: Boolean) {
        val layout = when (index) {
            0 -> binding.layoutSecond
            1 -> binding.layoutthird
            2 -> binding.layoutFourth
            3 -> binding.layoutFifth
            4 -> binding.layoutSixth
            5 -> binding.layoutSeventh
            6 -> binding.layoutEighth
            7 -> binding.layoutNinth
            8 -> binding.layoutTenth
            else -> null
        }
        layout?.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (!isVisible) clearLayout(layout!!)
    }

    // Collect data from all visible layouts
    private fun collectDataFromLayouts(): List<Map<String, String?>> {
        val dataList = mutableListOf<Map<String, String?>>()
        val layouts = listOf(
            binding.etSubjectName to Pair(binding.etSubjectName, binding.rdtMark),
            binding.layoutSecond to Pair(binding.SubjectName2, binding.rdtMark2),
            binding.layoutthird to Pair(binding.SubjectName3, binding.rdtMark3),
            binding.layoutFourth to Pair(binding.SubjectName4, binding.rdtMark4),
            binding.layoutFifth to Pair(binding.SubjectName5, binding.rdtMark5),
            binding.layoutSixth to Pair(binding.SubjectName6, binding.rdtMark6),
            binding.layoutSeventh to Pair(binding.SubjectName7, binding.rdtMark7),
            binding.layoutEighth to Pair(binding.SubjectName8, binding.rdtMark8),
            binding.layoutNinth to Pair(binding.SubjectName9, binding.rdtMark9),
            binding.layoutTenth to Pair(binding.SubjectName10, binding.rdtMark10)
        )

        layouts.forEachIndexed { index, (layout, fields) ->
            if (layout.visibility == View.VISIBLE) {
                val subjectName = fields.first.text.toString()
                val marks = fields.second.text.toString()
                val selectedClassName = binding.SelectClassId.selectedItem.toString()

                // Ensure subject name and marks are not empty
                if (subjectName.isNotBlank() && marks.isNotBlank()) {
                    dataList.add(
                        mapOf(
                            "layout_index" to index.toString(),
                            "classes" to selectedClassName, // Add selected class name here
                            "subject_name" to subjectName,
                            "marks" to marks,
                            "school_id" to SchoolId().getSchoolId(requireContext())
                        )
                    )
                }
            }
        }
        return dataList
    }

    private fun clearLayout(layout: ViewGroup) {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            when (child) {
                is EditText -> child.text.clear()
                is ViewGroup -> clearLayout(child)
            }
        }
    }

    private fun initObservers() {
        viewModel.apiResponse.observe(viewLifecycleOwner) { response ->
            Log.d("Observer", "apiResponse triggered: ${response.message}")

            if (response.message.isNotEmpty()) {
                Log.d("Observer", "Showing confirmation dialog")

                // Show the confirmation dialog
                toolBox.showConfirmationDialog(
                    context = requireContext(),
                    title = "Assigned Subject Successfully",
                    message = "Would you like to add more subjects?",
                    positiveButtonText = "YES",
                    positiveButtonAction = {
                        toolBox.replaceFragment(AssignSubject(), requireContext())
                    },
                    negativeButtonText = "NO",
                    negativeButtonAction = {
                        toolBox.fragmentChanger(ClassWithSubject(), requireContext())
                    },
                    cancelable = false
                )

                // Remove the observer after it's triggered once
                viewModel.apiResponse.removeObservers(viewLifecycleOwner)
            }
        }
    }


//  ---------------Calling class API to show all class data in spinner ----------
//    private fun fetchClassNames(schoolId: String) {
//        RetrofitHelper.getApiService().getClassList(schoolId).enqueue(object : Callback<AllClassNameResponse> {
//            override fun onResponse(call: Call<AllClassNameResponse>, response: Response<AllClassNameResponse>) {
//                if (response.isSuccessful) {
//                    val allClassNameResponse = response.body()
//
//                    if (allClassNameResponse != null && allClassNameResponse.status) {
//                        // Extract the class names from the response
//
//                        val classNames = allClassNameResponse.classes.map { it.class_name }
//
//                        // Set the class names to the Spinner
//                        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, classNames )
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        binding.SelectClass.adapter = adapter
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<AllClassNameResponse>, t: Throwable) {
//                // Handle failure
//                t.printStackTrace()
//            }
//        })
//    }

    //  ---------------Start Calling class API to show all class data in spinner ----------
    private fun viewModelAllClass(){
        val factory = AllClassViewModelFactory(AllClassRepository())
        viewModelAllClassAssignSubejct = ViewModelProvider(this, factory).get(AllClassViewModel::class.java)

    }

    private fun setupAllClassObserver() {
        try {
            viewModelAllClassAssignSubejct.getClasses(SchoolId().getSchoolId(requireContext())).observe(viewLifecycleOwner) { response ->
                response?.data?.let { classList ->
                    if (classList.isNotEmpty()) {
                        toolBox.stopLoadingBar()
                        // Extract the class names and class IDs from the classList
                        val classNames = mutableListOf("Select Class") // Add "Select Class" as the default item
                        val classIds = mutableListOf("Select Class ID") // Add "Select Class ID" as the default item

                        // Add class names and class IDs from the class list
                        classNames.addAll(classList.map { it.class_name })
                        classIds.addAll(classList.map { it.class_id.toString() })
                        // Create adapter for the first spinner with class names (including "Select Class")
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        // Create adapter for the second spinner with class IDs (including "Select Class ID")
                        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classIds)
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        // Set the adapters to the respective Spinners
                        binding.SelectClass.adapter = adapter
                        binding.SelectClassId.adapter = adapter2

                        // Set the OnItemSelectedListener for the first Spinner
                        binding.SelectClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                                // Get the selected class name (from first spinner)
                                val selectedClassName = parentView.getItemAtPosition(position) as String

                                // Filter class IDs based on the selected class name
                                val filteredClassIds = classList.filter { it.class_name == selectedClassName }
                                    .map { it.class_id.toString() }

                                // Ensure filteredClassIds is not empty before updating the second spinner
                                if (filteredClassIds.isNotEmpty()) {
                                    // Update the second spinner with the filtered class IDs
                                    val updatedAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filteredClassIds)
                                    updatedAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.SelectClassId.adapter = updatedAdapter2
                                } else {
                                    // If no matching class IDs, set the default "Select Class ID"
                                    binding.SelectClassId.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Class ID"))
                                }
                            }

                            override fun onNothingSelected(parentView: AdapterView<*>) {
                                // Handle case when nothing is selected (optional)
                                // Set the default "Select Class ID" in the second spinner
                                binding.SelectClassId.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Class ID"))
                            }
                        }
                    } else {
                        toolBox.showConfirmationDialog(
                            context = requireContext(),
                            title = "No classes are available.",
                            message = "Would you like to add a class now?",
                            positiveButtonText = "YES",
                            positiveButtonAction = { toolBox.fragmentChanger(NewClass(), requireContext()) },
                            negativeButtonText = "NO",
                            negativeButtonAction = { toolBox.activityChanger(MainActivity(), requireContext())},
                            cancelable = false)
                        Toast.makeText(requireContext(), "No classes found", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    toolBox.stopLoadingBar()
                    toolBox.showConfirmationDialog(
                        context = requireContext(),
                        title = "No classes are available.",
                        message = "Would you like to add a class now?",
                        positiveButtonText = "YES",
                        positiveButtonAction = { toolBox.fragmentChanger(NewClass(), requireContext()) },
                        negativeButtonText = "NO",
                        negativeButtonAction = { toolBox.activityChanger(MainActivity(), requireContext())},
                        cancelable = false)
//                    toolBox.showConfirmationDialog(
//                        context = requireContext(),
//                        title = "Failed to Load !",
//                        message = "Connection failed please check mobile data or wifi and try again.",
//                        positiveButtonText = "ok",
//                        positiveButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
//                        negativeButtonText = "Refresh",
//                        negativeButtonAction = {toolBox.fragmentChanger(AssignSubject(), requireContext()) },
//                        cancelable = false
//                    )
                        //Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (E : Exception){
            toolBox.showConfirmationDialog(
                context = requireContext(),
                title = "Warning !",
                message = "Unknown Error found Please Contact with developer nurkude@gmail.com ",
                positiveButtonText = "OK",
                positiveButtonAction = { toolBox.activityChanger(MainActivity(), requireContext()) },
                negativeButtonText = "",
                negativeButtonAction = {  },
                cancelable = false)
        }
    }
}




