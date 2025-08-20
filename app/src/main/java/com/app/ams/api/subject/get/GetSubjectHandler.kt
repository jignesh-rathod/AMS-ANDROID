package com.app.ams.api.subject.get

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetSubjectHandler
{
    companion object
    {
        fun getSubjects(context: Context, semesterId: Int): ApiResponse = Api.call(path = "/subject/get?semesterid=$semesterId", context = context)

        fun ApiResponse.asGetSubjectResponse(): GetSubjectResponse = Gson().fromJson(this.json, GetSubjectResponse::class.java)
    }
}