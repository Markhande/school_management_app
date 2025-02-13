package com.example.schoolerp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.DataClasses.AcountChartData
import com.example.schoolerp.R

class AcountChartAdapterIncome(
    private val dataList: MutableList<AcountChartData>,
    private val itemClickListener: (AcountChartData) -> Unit // Lambda to handle item clicks
) : RecyclerView.Adapter<AcountChartAdapterIncome.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textViewItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ecycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.textView.text = item.head

        // Set click listener on each item in the RecyclerView
        holder.itemView.setOnClickListener {
            itemClickListener(item)  // Invoke the lambda function
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<AcountChartData>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }
}
