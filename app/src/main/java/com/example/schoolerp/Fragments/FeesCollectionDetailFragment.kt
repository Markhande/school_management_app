package com.example.schoolerp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.helpers.MethodLibrary
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.FeeParticularData
import com.example.schoolerp.Fragments.Fragment.FeeParticular
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.databinding.FragmentFeesCollectionDetailBinding
import com.example.schoolerp.repository.UpatedFeesCollectionRepository
import com.example.schoolerp.viewmodel.UpateFeesCollectionViewModel
import com.example.schoolerp.viewmodelfactory.UpdateFeesCollectionViewModelFactory

class FeesCollectionDetailFragment : Fragment() {
    private lateinit var binding: FragmentFeesCollectionDetailBinding
    val toolBox = MethodLibrary()

    private val dynamicFineLabels = mutableListOf<EditText>()
    private val dynamicFineAmounts = mutableListOf<EditText>()
    private lateinit var feeParticularViewModelUpdate: UpateFeesCollectionViewModel

    private lateinit var student_ID:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeesCollectionDetailBinding.inflate(inflater, container, false)
        val feeParticularDataList = arguments?.getParcelableArrayList<FeeParticularData>("Fees")
        feeParticularDataList?.let { populateFields(it) }

        ModelViewAddFeesTuitionUpdate()
        setUpLisners()
        return binding.root
    }

    private fun populateFields(data: List<FeeParticularData>) {
        val firstData = data.first()

        binding.apply {
            name.text = firstData.st_name
            schoolId.text = firstData.school_id
            id.text = firstData.id
            clasName.text = firstData.classes
            SelectionSpecific.text = firstData.search_specific
            previousbalanceamount.setText(firstData.previous_deposit_amount)
            DiscountAmount.setText(firstData.discount_fee)
            TotalAmount.setText(firstData.row_total_amount)
            //depositeAmount.setText(firstData.deposite_amount)
            DueAmount.setText(firstData.due_balance)
            student_ID = firstData.student_id


            val particularLabels = firstData.particular_label ?: arrayListOf()
            val prefixAmounts = firstData.prefix_amount ?: arrayListOf()

            Log.d("FeeParticular", "Labels: $particularLabels")
            Log.d("FeeParticular", "Amounts: $prefixAmounts")

            AdmissionFees.setText(particularLabels.getOrNull(0))
            RegistrationFees.setText(particularLabels.getOrNull(1))
            ArtMaterial.setText(particularLabels.getOrNull(2))
            Transport.setText(particularLabels.getOrNull(3))
            Books.setText(particularLabels.getOrNull(4))
            Uniform.setText(particularLabels.getOrNull(5))
            Fine.setText(particularLabels.getOrNull(6))
            TermlyTuitionFees.setText(particularLabels.getOrNull(7))

            AdmissionFeesPrefixamount.setText(prefixAmounts.getOrNull(0))
            AmountRegistrationFees.setText(prefixAmounts.getOrNull(1))
            AmountArtMaterial.setText(prefixAmounts.getOrNull(2))
            AmountTransport.setText(prefixAmounts.getOrNull(3))
            AmountBooks.setText(prefixAmounts.getOrNull(4))
            AmountUniform.setText(prefixAmounts.getOrNull(5))
            AmountFine.setText(prefixAmounts.getOrNull(6))
            tuitionFeesTextView.setText(prefixAmounts.getOrNull(7))
        }
    }

    private fun collectFeeData(): FeeParticularData {
        val particularLabels = listOf(
            binding.AdmissionFees.text.toString(),
            binding.RegistrationFees.text.toString(),
            binding.ArtMaterial.text.toString(),
            binding.Transport.text.toString(),
            binding.Books.text.toString(),
            binding.Uniform.text.toString(),
            binding.Fine.text.toString(),
            binding.TermlyTuitionFees.text.toString()
        ).filter { it.isNotBlank() }

        val prefixAmounts = listOf(
            binding.AdmissionFeesPrefixamount.text.toString(),
            binding.AmountRegistrationFees.text.toString(),
            binding.AmountArtMaterial.text.toString(),
            binding.AmountTransport.text.toString(),
            binding.AmountBooks.text.toString(),
            binding.AmountUniform.text.toString(),
            binding.AmountFine.text.toString(),
            binding.tuitionFeesTextView.text.toString()
        ).filter { it.isNotBlank() }

        val dynamicLabels = dynamicFineLabels.map { it.text.toString() }.filter { it.isNotBlank() }
        val dynamicAmounts =
            dynamicFineAmounts.map { it.text.toString() }.filter { it.isNotBlank() }

        val allLabels = ArrayList(particularLabels + dynamicLabels)
        val allAmounts = ArrayList(prefixAmounts + dynamicAmounts)

        Log.d("FeeParticular", "All Labels: $allLabels")
        Log.d("FeeParticular", "All Amounts: $allAmounts")

        return FeeParticularData(
            id = binding.id.text.toString(),
            classes = binding.clasName.text.toString(),
            search_specific = binding.SelectionSpecific.text.toString(),
            particular_label = allLabels, // Ensure this is a `List<String>`
            prefix_amount = allAmounts, // Ensure this is a `List<String>`
            previous_deposit_amount = binding.previousbalanceamount.text.toString(),
            discount_fee = binding.DiscountAmount.text.toString(),
            row_total_amount = binding.TotalAmount.text.toString(),
            due_balance = binding.DueAmount.text.toString().takeIf { it.isNotEmpty() },
            deposite_amount = binding.depositeAmount.text.toString().takeIf { it.isNotEmpty() },
            school_id = binding.schoolId.text.toString(),
            created_at = "2024-12-01", // Replace with a dynamic value if required
            updated_at = "2024-12-01", // Replace with a dynamic value if required
            registration_no = "", // Replace with a dynamic value if required
            class_name = "", // Replace with a dynamic value if required
            st_name = binding.name.text.toString(),
            student_id = binding.name.text.toString()
        )
    }

    private fun ModelViewAddFeesTuitionUpdate() {
        val apiService = RetrofitHelper.getApiService()
        val repository = UpatedFeesCollectionRepository(apiService)
        val factory = UpdateFeesCollectionViewModelFactory(repository)
        feeParticularViewModelUpdate =
            ViewModelProvider(this, factory).get(UpateFeesCollectionViewModel::class.java)

    }

    private fun setUpLisners() {
        binding.btnAddPartculaarFees.setOnClickListener {
            UpadateFees()
        }
    }

    private fun UpadateFees() {
        val schoolId = binding.schoolId.text.toString()
        val className = binding.clasName.text.toString()
        val specific = binding.SelectionSpecific.text.toString()
        val Id = binding.id.text.toString()
        val previousbalanceamount = binding.previousbalanceamount.text.toString()
        val DiscountAmount = binding.DiscountAmount.text.toString()
        val TotalAmount = binding.TotalAmount.text.toString()
        val depositeAmount = binding.depositeAmount.text.toString()
        val DueAmount = binding.DueAmount.text.toString()


        val particularLabels = listOf(
            binding.AdmissionFees.text.toString(),
            binding.RegistrationFees.text.toString(),
            binding.ArtMaterial.text.toString(),
            binding.Transport.text.toString(),
            binding.Books.text.toString(),
            binding.Uniform.text.toString(),
            binding.Fine.text.toString(),
            binding.TermlyTuitionFees.text.toString()
        ).filter { it.isNotBlank() }

        val prefixAmounts = listOf(
            binding.AdmissionFeesPrefixamount.text.toString(),
            binding.AmountRegistrationFees.text.toString(),
            binding.AmountArtMaterial.text.toString(),
            binding.AmountTransport.text.toString(),
            binding.AmountBooks.text.toString(),
            binding.AmountUniform.text.toString(),
            binding.AmountFine.text.toString(),
            binding.tuitionFeesTextView.text.toString()
        ).filter { it.isNotBlank() }

        val dynamicLabels = dynamicFineLabels.map { it.text.toString() }.filter { it.isNotBlank() }
        val dynamicAmounts =
            dynamicFineAmounts.map { it.text.toString() }.filter { it.isNotBlank() }

        val allLabels = particularLabels + dynamicLabels
        val allAmounts = prefixAmounts + dynamicAmounts

        Log.d("FeeParticular", "Particular Labels: $allLabels")
        Log.d("FeeParticular", "Prefix Amounts: $allAmounts")

        if (allLabels.size == allAmounts.size) {
            val formattedLabels = allLabels.joinToString(",")
            val formattedAmounts = allAmounts.joinToString(",")

//            val params = mapOf(
//                "classes" to className,
//                "search_specific" to specific,
//                "school_id" to schoolId,
//                "particular_label" to formattedLabels,
//                "prefix_amount" to formattedAmounts,
//                //  "previous_deposit_amount" to previousbalanceamount,
//                "discount_fee" to DiscountAmount,
//                "row_total_amount" to TotalAmount,
//                "deposite_amount" to depositeAmount,
//                // "due_balance" to DueAmount,
//                "id" to Id
//            )

            val params = mapOf(
                "school_id" to SchoolId().getSchoolId(requireContext()),
                "student_id" to student_ID,
                "deposite_amount" to depositeAmount,
            )

            val paramsLog = params.entries.joinToString(", ") { "${it.key}=${it.value}" }
            Log.d("FeeParticular", "Parameters to API: {$paramsLog}")

            feeParticularViewModelUpdate.editColleaction(params)

            feeParticularViewModelUpdate.editStudentResult.observe(viewLifecycleOwner) { response ->
                response.fold(
                    onSuccess = {
                        // Handle success
                        Log.d("FeeParticular", "Data updated: ${it.message}")
                        showAddFeeParticularDialog()
                    },
                    onFailure = {
                        // Handle failure
                        Log.e("FeeParticular", "Error: ${it.message}")
                        Toast.makeText(context, "Failed to update fees.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        } else {
            Log.e("FeeParticular", "Mismatch between labels and amounts.")
            Toast.makeText(context, "Mismatch between labels and amounts.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showAddFeeParticularDialog() {
        toolBox.showConfirmationDialog(
            context = requireContext(),
            title = "Data Added Successfully",
            message = "Would You Like Add More Deposit",
            positiveButtonText = "Yes",
            positiveButtonAction = { toolBox.fragmentChanger(CollectionFee(), requireContext()) },
            negativeButtonText = "No",
            negativeButtonAction = { toolBox.fragmentChanger(DashBoard(), requireContext()) },
            cancelable = false
        )
    }
}