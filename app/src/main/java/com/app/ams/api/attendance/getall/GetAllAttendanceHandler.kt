package com.app.ams.api.attendance.getall

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetAllAttendanceHandler
{
    companion object
    {
        fun getAllAttendance(context: Context): ApiResponse = Api.call(path = "/attendance/getall", context = context)

        fun ApiResponse.asGetAllAttendanceResponse(): GetAllAttendanceResponse = Gson().fromJson(this.json, GetAllAttendanceResponse::class.java)
    }
}