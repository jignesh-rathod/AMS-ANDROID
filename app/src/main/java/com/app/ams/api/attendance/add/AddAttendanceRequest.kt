package com.app.ams.api.attendance.add

import com.app.ams.api.student.getforattendance.models.Student

data class AddAttendanceRequest(
    val divisionId: Int,
    val subjectId: Int,
    val lectureId: Int,
    val date: String,
    var studentRecords: ArrayList<Student>
)
