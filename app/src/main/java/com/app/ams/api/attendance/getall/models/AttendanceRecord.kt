package com.app.ams.api.attendance.getall.models

data class AttendanceRecord(
    val date: String,
    val time: String,
    val facultyName: String,
    val subjectName: String,
    val semester: Int,
    val division: String,
    val present: Int,
    val absent: Int
)
