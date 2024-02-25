package com.app.ams.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ams.R
import com.app.ams.api.student.getforattendance.models.Student

class StudentForAttendanceRVAdapter(
    private val context: Context,
    private val resourceId: Int,
    private val students: ArrayList<Student>,
    private val onCheckedChangeListener: OnCheckedChangeListener
) : RecyclerView.Adapter<StudentForAttendanceRVAdapter.ViewHolder>()
{

    interface OnCheckedChangeListener
    {
        fun onCheckedChange(position: Int, isChecked: Boolean)
    }

    class ViewHolder(view: View, private val onCheckedChangeListener: OnCheckedChangeListener) : RecyclerView.ViewHolder(view)
    {
        val checkboxIsPresent: CheckBox = view.findViewById(R.id.checkboxIsPresent)
        val tvRollNumber: TextView = view.findViewById(R.id.tvRollNumber)
        val tvName: TextView = view.findViewById(R.id.tvName)

        init
        {
            checkboxIsPresent.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChangeListener.onCheckedChange(adapterPosition, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        return ViewHolder(view, onCheckedChangeListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.checkboxIsPresent.isChecked = students[position].isPresent
        holder.tvRollNumber.text = students[position].rollNumber
        holder.tvName.text = students[position].name
    }

    override fun getItemCount(): Int
    {
        return students.size
    }
}
