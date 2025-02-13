package com.example.schoolerp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.BankStatementData
import com.example.schoolerp.R

class BankStatementAdapter(private val statementList: List<BankStatementData>) :
    RecyclerView.Adapter<BankStatementAdapter.BankStatementViewHolder>() {

    // ViewHolder class that holds references to each view in the item layout
    class BankStatementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvDebit: TextView = itemView.findViewById(R.id.tvDebit)
        val tvCredit: TextView = itemView.findViewById(R.id.tvCredit)
        val tvNetBalance: TextView = itemView.findViewById(R.id.tvNetBalance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankStatementViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bank_statement, parent, false)
        return BankStatementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BankStatementViewHolder, position: Int) {
        val currentItem = statementList[position]

        holder.tvDate.text = currentItem.date
        holder.tvDescription.text = currentItem.description
        holder.tvDebit.text = currentItem.debit
        holder.tvCredit.text = currentItem.credit
        holder.tvNetBalance.text = currentItem.netBalance

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context,R.color.info_bg)
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.white)
            )
        }
    }

    override fun getItemCount() = statementList.size
}
