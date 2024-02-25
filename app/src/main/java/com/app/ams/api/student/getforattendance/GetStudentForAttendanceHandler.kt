package com.app.ams.api.student.getforattendance

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetStudentForAttendanceHandler
{
    companion object
    {
        fun getStudents(context: Context, batchId: Int, semesterId: Int, divisionId: Int): ApiResponse =
            Api.call(path = "/student/getforattendance?batchid=$batchId&semesterid=$semesterId&divisionid=$divisionId", context = context)

        fun ApiResponse.asGetStudentForAttendanceResponse(): GetStudentForAttendanceResponse = Gson().fromJson(this.json, GetStudentForAttendanceResponse::class.java)
    }
}