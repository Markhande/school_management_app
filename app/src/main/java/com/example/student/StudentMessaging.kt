package com.example.student

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Activities.MainActivity
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.GetMessageData
import com.example.schoolerp.Fragments.AddNewEmployees
import com.example.schoolerp.Fragments.AllEmployees
import com.example.schoolerp.R
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentStudentMessagingBinding
import com.example.schoolerp.repository.GetMessageRepository
import com.example.schoolerp.viewmodel.GetMessageViewModel
import com.example.schoolerp.viewmodelfactory.GetMessageViewModelFactory
import com.example.student.Adapter.GetStudentMeassageAdapter
import com.example.teacher.TeacherDetails
import kotlin.math.truncate

class StudentMessaging : Fragment() {
    private lateinit var viewModelGetmessage: GetMessageViewModel
    private lateinit var getmessageadapter: GetStudentMeassageAdapter
    private lateinit var binding: FragmentStudentMessagingBinding
    val toolbox = MethodLibrary()
    var isExpanded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Bind the layout
        binding = FragmentStudentMessagingBinding.inflate(inflater, container, false)

        setupRecyclerView()

        binding.writeMessage.setOnClickListener {
            animationLinearLayout(
                binding.layoutVisible,
                "Hide My Personal Information",
                120,
                569,
                500
            )
        }

        toolbox.clicked(binding.refreshImageLayout){
            setupRecyclerView()
            toolbox.rotateImage(
                imageView =  binding.refreshImage,
                duration = 1000
            )
        }
        return binding.root
    }

    // Initialize the ViewModel
    private fun initializeViewModel() {

    }

    // Set up RecyclerView with an adapter
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.GetMessageRecycler.layoutManager = layoutManager
        getmessageadapter = GetStudentMeassageAdapter(emptyList())
        binding.GetMessageRecycler.adapter = getmessageadapter
        observeMessages()
    }

    private fun observeMessages() {
        val apiService = RetrofitHelper.getApiService()
        val repository = GetMessageRepository(apiService)
        val factory = GetMessageViewModelFactory(repository)
        viewModelGetmessage = ViewModelProvider(this, factory).get(GetMessageViewModel::class.java)


        val stName = StudentInfo().getStudentName(requireContext()) // Get student's name
        val stClass = StudentInfo().getStudentClass(requireContext()) // Get student's class

// Log the current student name and class for debugging purposes
        Log.d("GetMessageFragment", "Student Name: $stName")
        Log.d("GetMessageFragment", "Student Class: $stClass")

// Fetch the messages from the view model
        viewModelGetmessage.fetchMessages(SchoolId().getSchoolId(requireContext()), TeacherDetails().getTeacherId(requireContext()))

// Observing the response from the view model
        viewModelGetmessage.messages.observe(viewLifecycleOwner) { response ->
        try{
            if (response != null) {
                val data = response.data.reversed() ?: emptyList()

                Log.d("GetMessageFragment", "Fetched ${data.size} total messages.")

                // Filter the messages by class
                val filteredMessagesbyClass = data.filter { message ->
                    val isClassMatch = message.search_specific.equals(stClass, ignoreCase = true)
                    // Log if the message matches the class
                    Log.d(
                        "GetMessageFragment",
                        "Message: ${message.search_specific}, Class Match: $isClassMatch"
                    )
                    isClassMatch
                }

                // Filter the messages by student name (assuming the sender or some other field matches the student's name)
                val filteredMessagesByStName = data.filter { message ->
                    // Ensure that we match only messages where search_specific equals the student's name (stName)
                    val isNameMatch = message.search_specific.equals(stName, ignoreCase = true)
                    // Log if the message matches the student's name
                    Log.d(
                        "GetMessageFragment",
                        "Message: ${message.search_specific}, Name Match: $isNameMatch"
                    )
                    isNameMatch
                }

                // Merge the filtered lists (combine both class and name-based filters)
                val filteredMessages = filteredMessagesbyClass + filteredMessagesByStName

                // Log the merged filtered messages
                Log.d("GetMessageFragment", "Filtered messages: $filteredMessages")

                // Check if there are any matching messages
                if (filteredMessages.isNotEmpty()) {
                    // Pass the merged filtered data to the adapter to update the RecyclerView
                    getmessageadapter.submitList(filteredMessages)
                    // Log the number of messages passed to the adapter
                    Log.d(
                        "GetMessageFragment",
                        "Messages passed to adapter: ${filteredMessages.size}"
                    )
                    // Optionally, toggle an empty view if no matching data is found
                    // toggleEmptyView(false) // Show RecyclerView
                } else {
                    toolbox.showConfirmationDialog(
                        context = requireContext(),
                        title = "No Massage Are Available.",
                        positiveButtonText = "ok",
                        positiveButtonAction = { toolbox.activityChanger(MainActivity(), requireContext()) },
                        negativeButtonText = "",
                        negativeButtonAction = {  },
                        cancelable = false)
                    Log.d("GetMessageFragment", "No matching messages found.")
                    // toggleEmptyView(true) // Show empty view (no data)
                }
            } else {
                //Toast.makeText(requireContext(), "failed to fatched", Toast.LENGTH_SHORT).show()
                Log.e("GetMessageFragment", "Failed to fetch messages.")
                // toggleEmptyView(true) // Show empty view (no data)
            }
        }catch (e:Exception){
            toolbox.showConfirmationDialog(
                context = requireContext(),
                title = "No Massage Are Available.",
                positiveButtonText = "ok",
                positiveButtonAction = { toolbox.activityChanger(MainActivity(), requireContext()) },
                negativeButtonText = "",
                negativeButtonAction = {  },
                cancelable = false)
        }

        }
    }


        // Animation to expand/collapse the layout
    private fun animationLinearLayout(
        linearLayout: View,
        endName: String,
        startLayoutHeight: Int,
        targetLayoutHeight: Int,
        timeDuration: Long
    ) {
        val startHeight = linearLayout.height
        val targetHeight: Int

        // Toggle between expanded and collapsed states
        if (isExpanded) {
            targetHeight = (resources.displayMetrics.density * startLayoutHeight).toInt()
            binding.tvSample.text = "Show My Personal Information"
        } else {
            targetHeight = (resources.displayMetrics.density * targetLayoutHeight).toInt()
            binding.tvSample.text = endName
        }

        val animator = ValueAnimator.ofInt(startHeight, targetHeight)
        animator.duration = timeDuration
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val params = linearLayout.layoutParams
            params.height = value
            linearLayout.layoutParams = params
        }
        animator.start()
        isExpanded = !isExpanded
    }
}
