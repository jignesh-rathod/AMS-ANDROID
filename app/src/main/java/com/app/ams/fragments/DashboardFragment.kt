package com.app.ams.fragments

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ams.R
import com.app.ams.adapters.AllAttendanceRVAdapter
import com.app.ams.api.attendance.getall.GetAllAttendanceHandler
import com.app.ams.api.attendance.getall.GetAllAttendanceHandler.Companion.asGetAllAttendanceResponse
import com.app.ams.api.attendance.getall.models.AttendanceRecord
import com.app.ams.api.division.get.GetDivisionHandler
import com.app.ams.api.faculty.get.GetFacultyHandler
import com.app.ams.api.faculty.get.GetFacultyHandler.Companion.asGetFacultyResponse
import com.app.ams.api.student.get.GetStudentHandler
import com.app.ams.dialogs.SessionExpireDialog
import com.app.ams.models.DateDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.util.*
import kotlin.collections.ArrayList

class DashboardFragment : Fragment()
{
    private lateinit var rootView: View
    private lateinit var context: Context

    private lateinit var tvFacultiesCount: TextView
    private lateinit var tvStudentsCount: TextView
    private lateinit var tvClassesCount: TextView
    private lateinit var tvTotalAttendanceCount: TextView
    private lateinit var rvAttendance: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)
        context = requireContext()

        initialize()

        CoroutineScope(Dispatchers.IO).launch {
            fetchFaculties()
            fetchStudents()
            fetchDivisions()
            fetchAllAttendance()
        }

        return rootView
    }

    private fun initialize()
    {
        tvFacultiesCount = rootView.findViewById(R.id.tvFacultiesCount)
        tvStudentsCount = rootView.findViewById(R.id.tvStudentsCount)
        tvClassesCount = rootView.findViewById(R.id.tvClassesCount)
        tvTotalAttendanceCount = rootView.findViewById(R.id.tvTotalAttendanceCount)
        rvAttendance = rootView.findViewById(R.id.rvAttendance)
    }

    private suspend fun fetchFaculties()
    {
        val response = GetFacultyHandler.getFaculties(context)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(context)
                }
            }

            HttpURLConnection.HTTP_OK ->
            {
                val data = response.asGetFacultyResponse()
                withContext(Dispatchers.Main) {
                    tvFacultiesCount.text = data.totalResults.toString()
                }
            }
        }
    }

    private suspend fun fetchStudents()
    {
        val response = GetStudentHandler.getStudents(context)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(context)
                }
            }

            HttpURLConnection.HTTP_OK ->
            {
                val data = response.asGetFacultyResponse()
                withContext(Dispatchers.Main) {
                    tvStudentsCount.text = data.totalResults.toString()
                }
            }
        }
    }

    private suspend fun fetchDivisions()
    {
        val response = GetDivisionHandler.getDivisions(context)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(context)
                }
            }

            HttpURLConnection.HTTP_OK ->
            {
                val data = response.asGetFacultyResponse()
                withContext(Dispatchers.Main) {
                    tvClassesCount.text = data.totalResults.toString()
                }
            }
        }
    }

    private suspend fun fetchAllAttendance()
    {
        val response = GetAllAttendanceHandler.getAllAttendance(context)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(context)
                }
            }

            HttpURLConnection.HTTP_OK ->
            {
                val data = response.asGetAllAttendanceResponse()
                val todayAttendance = data.attendanceRecords.filter {
                    isToday(it.date)
                }

                withContext(Dispatchers.Main) {
                    tvTotalAttendanceCount.text = data.totalResults.toString()

                    val allAttendanceRVAdapter = AllAttendanceRVAdapter(
                        context,
                        R.layout.list_item_for_today_attendance,
                        ArrayList<AttendanceRecord>(todayAttendance)
                    )

                    val layoutParams: ViewGroup.LayoutParams = rvAttendance.layoutParams
                    layoutParams.height = allAttendanceRVAdapter.itemCount * dpToPx(122)
                    rvAttendance.layoutParams = layoutParams

                    rvAttendance.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    rvAttendance.adapter = allAttendanceRVAdapter
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int
    {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    private fun fetchCurrentDate(): DateDetails
    {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        return DateDetails(day, month, year)
    }

    private fun isToday(date: String): Boolean
    {
        val currentDate = fetchCurrentDate()
        val temp = date.split("-")
        val attendanceDate = DateDetails(
            day = temp[2].toInt(),
            month = temp[1].toInt() - 1,
            year = temp[0].toInt()
        )

        return currentDate == attendanceDate
    }
}