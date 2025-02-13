package com.example.schoolerp.Adapter

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolerp.R

class EmployeeAttendanceReportAdapter(private val itemList: List<ClipData.Item>) :
    RecyclerView.Adapter<EmployeeAttendanceReportAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textSrNo : TextView = view.findViewById(R.id.srNo)
        val textName: TextView = view.findViewById(R.id.employeeName)
        val textRole: TextView = view.findViewById(R.id.employeeRole)
        val listItemBody: View = view.findViewById(R.id.listItemBody)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employeeattendancereport, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]

        holder.textSrNo.text = "${position + 1}."
        holder.textName.text = item.text
        holder.textRole.text = item.htmlText


        val color = if (position % 2 == 0) {
            ContextCompat.getColor(holder.itemView.context, R.color.info_bg)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.white)
        }
        holder.listItemBody.setBackgroundColor(color)
    }

    override fun getItemCount(): Int = itemList.size
}
