package com.app.ams.fragments

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ams.models.DateDetails
import com.app.ams.R
import com.app.ams.adapters.AttendanceRVAdapter
import com.app.ams.api.attendance.get.GetAttendanceHandler
import com.app.ams.api.attendance.get.GetAttendanceHandler.Companion.asGetAttendanceResponse
import com.app.ams.api.attendance.get.models.AttendanceRecord
import com.app.ams.dialogs.SessionExpireDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.util.*


class StudentDashboardFragment : Fragment()
{
    private lateinit var rootView: View
    private lateinit var context: Context

    private lateinit var tvTotalLecturesCount: TextView
    private lateinit var tvAttendedLecturesCount: TextView
    private lateinit var tvAbsentLecturesCount: TextView
    private lateinit var tvAttendancePercentageCount: TextView
    private lateinit var rvAttendance: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        rootView = inflater.inflate(R.layout.fragment_student_dashboard, container, false)
        context = requireContext()

        initialize()

        CoroutineScope(Dispatchers.IO).launch {
            fetchAttendance()
        }

        return rootView
    }

    private fun initialize()
    {
        tvTotalLecturesCount = rootView.findViewById(R.id.tvTotalLecturesCount)
        tvAttendedLecturesCount = rootView.findViewById(R.id.tvAttendedLecturesCount)
        tvAbsentLecturesCount = rootView.findViewById(R.id.tvAbsentLecturesCount)
        tvAttendancePercentageCount = rootView.findViewById(R.id.tvAttendancePercentageCount)
        rvAttendance = rootView.findViewById(R.id.rvAttendance)
    }

    private suspend fun fetchAttendance()
    {
        val response = GetAttendanceHandler.getAttendance(context)

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
                val data = response.asGetAttendanceResponse()
                val todayAttendance = data.attendanceRecords.filter {
                    isToday(it.date)
                }

                withContext(Dispatchers.Main) {
                    tvTotalLecturesCount.text = data.totalLectures.toString()
                    tvAttendedLecturesCount.text = data.attendedLectures.toString()
                    tvAbsentLecturesCount.text = data.absentLectures.toString()
                    tvAttendancePercentageCount.text = data.attendancePercentage.toString()

                    val attendanceRVAdapter = AttendanceRVAdapter(
                        context,
                        R.layout.list_item_for_today_attendance_for_student,
                        ArrayList<AttendanceRecord>(todayAttendance)
                    )

                    val layoutParams: ViewGroup.LayoutParams = rvAttendance.layoutParams
                    layoutParams.height = attendanceRVAdapter.itemCount * dpToPx(122)
                    rvAttendance.layoutParams = layoutParams

                    rvAttendance.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    rvAttendance.adapter = attendanceRVAdapter
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