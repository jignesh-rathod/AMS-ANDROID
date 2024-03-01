package com.app.ams.api.user.updatepassword

data class UpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)
