package com.app.ams.api.faculty.get

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetFacultyHandler
{
    companion object
    {
        fun getFaculties(context: Context): ApiResponse = Api.call(path = "/faculty/get", context = context)

        fun ApiResponse.asGetFacultyResponse(): GetFacultyResponse = Gson().fromJson(this.json, GetFacultyResponse::class.java)
    }
}