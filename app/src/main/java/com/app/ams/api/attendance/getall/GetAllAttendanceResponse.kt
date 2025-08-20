package com.app.ams.api.attendance.getall

import com.app.ams.api.attendance.getall.models.AttendanceRecord

data class GetAllAttendanceResponse(
    val totalResults: Int,
    val attendanceRecords: ArrayList<AttendanceRecord>
)
