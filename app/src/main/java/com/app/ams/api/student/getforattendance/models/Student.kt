package com.app.ams.api.student.getforattendance.models

data class Student(
    val id: Int,
    val rollNumber: String,
    val name: String,
    var isPresent: Boolean = false
)
