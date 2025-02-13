package com.example.schoolerp.Adapter

import android.content.Context
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpers.MethodLibrary
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemJobLetterBinding
import com.example.schoolerp.util.ImageUtil

class JobLetterAdapater(
    private val context: Context,
    private var jobLetterList: List<AllEmployee>
) : RecyclerView.Adapter<JobLetterAdapater.JobLetterViewHolder>() {

    inner class JobLetterViewHolder(private val binding: ItemJobLetterBinding) :
        RecyclerView.ViewHolder(binding.root) {

            var toolbox=MethodLibrary()

        fun bind(allEmployee: AllEmployee) {
            binding.DateOfJoining.text = allEmployee.date_of_joining
            binding.usernameEmp.text = allEmployee.username
            binding.passwordemp.text = allEmployee.password
            binding.txtAccountStatus.text = allEmployee.st_status
            binding.empName.text = allEmployee.employee_name
            binding.empName1.text = allEmployee.employee_name

            binding.btnPrintjobletter.setOnClickListener {
                showPrintPreview(allEmployee) // Pass the clicked employee data to the print preview method
            }
            val imageUrl = ImageUtil.getFullImageUrl("employee", allEmployee.picture.toString())

            toolbox.displayImage(imageUrl,binding.imaemp,binding.root.context)

        }
    }

    private fun showPrintPreview(employee: AllEmployee) {
        // This is where you show the print preview or initiate the printing process.
        // Use the context passed to the adapter, not requireActivity()
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = JobLetterPrintAdapter(context, listOf(employee))  // Pass the specific employee's data
        printManager.print("Job Letter Print", printAdapter, null)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobLetterViewHolder {
        val binding = ItemJobLetterBinding.inflate(LayoutInflater.from(context), parent, false)
        return JobLetterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobLetterViewHolder, position: Int) {
        holder.bind(jobLetterList[position])
    }

    override fun getItemCount(): Int {
        return jobLetterList.size
    }

    fun updateData(newEmployees: List<AllEmployee>) {
        jobLetterList = newEmployees
        notifyDataSetChanged()
    }
}
