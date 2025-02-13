package com.example.schoolerp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.AcountChartData
import com.example.schoolerp.R
import com.example.schoolerp.databinding.ItemAccountChartBinding

class AcountChartAdapter(
    private var dataList: MutableList<AcountChartData>,
    private val deleteListener: OnItemDeleteListener // Add deleteListener parameter
) : RecyclerView.Adapter<AcountChartAdapter.ViewHolder>() {

    // Interface to handle item delete action
    interface OnItemDeleteListener {
        fun onItemDelete(acount: AcountChartData, position: Int)
    }

    // ViewHolder to hold the item views
    inner class ViewHolder(val binding: ItemAccountChartBinding) : RecyclerView.ViewHolder(binding.root)

    // Called to create the ViewHolder for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAccountChartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Bind data to each item in the RecyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val accountChartData = dataList[position]

        // Set text for the 'head' and 'account_type' fields
        holder.binding.accountHeadText.text = accountChartData.head
        holder.binding.accountTypeText.text = accountChartData.account_type

        // Alternate background colors for even and odd rows
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.info_bg)
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.white)
            )
        }

        // Handle the delete button click and call the listener
        holder.binding.ImgDelted.setOnClickListener {
            deleteListener.onItemDelete(accountChartData, position) // Trigger the delete action
        }
    }

    // Return the total number of items in the data list
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Method to update the data in the adapter and refresh the RecyclerView
    fun updateData(newDataList: List<AcountChartData>) {
        dataList = newDataList.toMutableList() // Update the data list with the new data
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}
