package com.app.ams.api.student.get.models

data class Student(
    val id: Int,
    val rollNumber: String,
    val name: String,
    val batch: String,
    val semester: Int,
    val division: String
)
