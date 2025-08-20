package com.app.ams.api.profile.get

data class GetProfileResponse(
    val userType: String,
    val enrollment: String,
    val rollNumber: String,
    val batch: String,
    val division: String,
    val semester: Int,
    val name: String,
    var contact: String,
    var email: String,
    var address: String,
    var imageName: String
)
