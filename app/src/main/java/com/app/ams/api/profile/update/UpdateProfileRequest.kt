package com.app.ams.api.profile.update

data class UpdateProfileRequest(
    var email: String,
    var contact: String,
    var address: String
)
