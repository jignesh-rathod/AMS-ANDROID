package com.app.ams.api.profile.get

import android.content.Context

import com.google.gson.Gson

import com.app.ams.api.Api
import com.app.ams.api.ApiResponse

class GetProfileHandler
{
    companion object
    {
        fun getProfile(context: Context): ApiResponse = Api.call(path = "/profile/get", context = context)

        fun ApiResponse.asGetProfileResponse(): GetProfileResponse = Gson().fromJson(this.json, GetProfileResponse::class.java)
    }
}