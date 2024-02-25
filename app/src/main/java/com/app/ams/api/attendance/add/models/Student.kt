package com.app.ams.api.attendance.add.models

data class Student(
    val id: Int,
    val rollNumber: String,
    val name: String,
    val isPresent: Boolean
)
