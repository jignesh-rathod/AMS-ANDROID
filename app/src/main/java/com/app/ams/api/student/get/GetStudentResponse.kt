package com.app.ams.api.student.get

import com.app.ams.api.student.get.models.Student

data class GetStudentResponse(
    val totalResults: Int,
    val students: ArrayList<Student>
)
