package com.app.ams.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.ams.R
import com.app.ams.api.attendance.get.models.AttendanceRecord

class AttendanceRVAdapter(
    private val context: Context,
    private val resourceId: Int,
    private val attendanceRecords: ArrayList<AttendanceRecord>,
) : RecyclerView.Adapter<AttendanceRVAdapter.ViewHolder>()
{
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvFacultyName: TextView = view.findViewById(R.id.tvFacultyName)
        val tvSubjectName: TextView = view.findViewById(R.id.tvSubjectName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.tvTime.text = attendanceRecords[position].time
        holder.tvFacultyName.text = attendanceRecords[position].facultyName
        holder.tvSubjectName.text = attendanceRecords[position].subjectName

        if (attendanceRecords[position].isPresent)
        {
            holder.tvStatus.text = ContextCompat.getString(context, R.string.present)
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
        else
        {
            holder.tvStatus.text = ContextCompat.getString(context, R.string.absent)
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
    }

    override fun getItemCount(): Int
    {
        return attendanceRecords.size
    }
}
