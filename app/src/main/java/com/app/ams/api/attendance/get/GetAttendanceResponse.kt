package com.app.ams.api.attendance.get

import com.app.ams.api.attendance.get.models.AttendanceRecord

data class GetAttendanceResponse(
    val totalLectures: Int,
    val attendedLectures: Int,
    val absentLectures: Int,
    val attendancePercentage: Float,
    val attendanceRecords: ArrayList<AttendanceRecord>
)