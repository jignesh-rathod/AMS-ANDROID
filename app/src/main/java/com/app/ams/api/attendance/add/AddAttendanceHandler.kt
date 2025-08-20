package com.app.ams.api.attendance.add

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class AddAttendanceHandler
{
    companion object
    {
        fun addAttendance(context: Context, data: AddAttendanceRequest): ApiResponse = Api.call(requestMethod = "POST", path = "/attendance/add", data = Gson().toJson(data), context = context)
    }
}