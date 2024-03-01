package com.app.ams.api.user.updatepassword

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class UpdatePasswordHandler
{
    companion object
    {
        fun updatePassword(context: Context, data: UpdatePasswordRequest): ApiResponse = Api.call(
            requestMethod = "POST",
            path = "/user/updatepassword",
            data = Gson().toJson(data),
            context = context
        )

        fun ApiResponse.asUpdatePasswordResponse(): UpdatePasswordResponse = Gson().fromJson(this.json, UpdatePasswordResponse::class.java)
    }
}