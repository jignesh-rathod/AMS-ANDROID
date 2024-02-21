package com.app.ams.api.profile.get

data class GetProfileResponse(
    val userType: String,
    val enrollment: String,
    val rollNumber: String,
    val batch: String,
    val division: String,
    val semester: Int,
    val name: String,
    val contact: String,
    val email: String,
    val address: String,
    val imageName: String
)
