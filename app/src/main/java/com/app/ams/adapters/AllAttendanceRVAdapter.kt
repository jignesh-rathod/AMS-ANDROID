package com.app.ams.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ams.R
import com.app.ams.api.attendance.getall.models.AttendanceRecord

class AllAttendanceRVAdapter(
    private val context: Context,
    private val resourceId: Int,
    private val attendanceRecords: ArrayList<AttendanceRecord>,
) : RecyclerView.Adapter<AllAttendanceRVAdapter.ViewHolder>()
{
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvFacultyName: TextView = view.findViewById(R.id.tvFacultyName)
        val tvSubjectAndSemesterAndDivision: TextView = view.findViewById(R.id.tvSubjectAndSemesterAndDivision)
        val tvPresentStudents: TextView = view.findViewById(R.id.tvPresentStudents)
        val tvAbsentStudents: TextView = view.findViewById(R.id.tvAbsentStudents)
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
        holder.tvPresentStudents.text = attendanceRecords[position].present.toString()
        holder.tvAbsentStudents.text = attendanceRecords[position].absent.toString()

        val semester = attendanceRecords[position].semester
        val division = attendanceRecords[position].division
        val subject = attendanceRecords[position].subjectName

        holder.tvSubjectAndSemesterAndDivision.text = "$semester - $division - $subject"
    }

    override fun getItemCount(): Int
    {
        return attendanceRecords.size
    }
}
