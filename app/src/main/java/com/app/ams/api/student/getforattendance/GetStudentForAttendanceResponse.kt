package com.app.ams.api.student.getforattendance

import com.app.ams.api.student.getforattendance.models.Student

data class GetStudentForAttendanceResponse(val totalResults: Int, val students: ArrayList<Student>)
