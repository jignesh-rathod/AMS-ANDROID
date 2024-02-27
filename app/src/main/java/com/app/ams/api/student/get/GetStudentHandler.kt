package com.app.ams.api.student.get

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetStudentHandler
{
    companion object
    {
        fun getStudents(context: Context): ApiResponse = Api.call(path = "/student/get", context = context)

        fun ApiResponse.asGetStudentResponse(): GetStudentResponse = Gson().fromJson(this.json, GetStudentResponse::class.java)
    }
}