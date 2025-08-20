package com.app.ams.api.attendance.get.models

data class AttendanceRecord(
    val date: String,
    val time: String,
    val facultyName: String,
    val subjectName: String,
    val isPresent: Boolean
)
