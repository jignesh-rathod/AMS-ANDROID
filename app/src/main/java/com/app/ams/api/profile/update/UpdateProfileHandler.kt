package com.app.ams.api.profile.update

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class UpdateProfileHandler
{
    companion object
    {
        fun updateProfile(context: Context, data: UpdateProfileRequest): ApiResponse = Api.call(
            requestMethod = "POST",
            path = "/profile/update",
            data = Gson().toJson(data),
            context = context
        )
    }
}