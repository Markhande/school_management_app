package com.example.student.Adapter

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.R
import com.example.schoolerp.models.responses.StudentFeeDetails
import com.example.student.StudentDetails
import com.example.student.StudentInfo

class studentFeeReceiptAdapter(private val studentFeesList: List<StudentFeeDetails>) :
    RecyclerView.Adapter<studentFeeReceiptAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stName: TextView = view.findViewById(R.id.stName)
        val stClass: TextView = view.findViewById(R.id.stClass)
        val feePaidDate: TextView = view.findViewById(R.id.feePaidDate)
        val stPaidAmount: TextView = view.findViewById(R.id.stPaidAmount)
        val totalAmount: TextView = view.findViewById(R.id.totalAmount)
        val feeDeposite: TextView = view.findViewById(R.id.feeDeposite)
        val stRemainingAmount: TextView = view.findViewById(R.id.stRemainingAmount)
        val discount: TextView = view.findViewById(R.id.discount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_studentfeereceipt, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.stName.text = StudentInfo().getStudentName(holder.itemView.context)
        holder.stClass.text = studentFeesList[position].class_name
        holder.feePaidDate.text = studentFeesList[position].created_at
        holder.stPaidAmount.text = studentFeesList[position].previous_deposit_amount
        holder.totalAmount.text = studentFeesList[position].row_total_amount
        holder.feeDeposite.text = studentFeesList[position].deposite_amount
        holder.stRemainingAmount.text = studentFeesList[position].due_balance
        holder.discount.text = studentFeesList[position].discount_fee
    }
    override fun getItemCount(): Int = studentFeesList.size
}