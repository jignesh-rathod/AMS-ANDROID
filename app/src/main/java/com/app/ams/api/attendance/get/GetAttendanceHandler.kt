package com.app.ams.api.attendance.get

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetAttendanceHandler
{
    companion object
    {
        fun getAttendance(context: Context): ApiResponse = Api.call(path = "/attendance/get", context = context)

        fun ApiResponse.asGetAttendanceResponse(): GetAttendanceResponse = Gson().fromJson(this.json, GetAttendanceResponse::class.java)
    }
}