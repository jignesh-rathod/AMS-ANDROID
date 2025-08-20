package com.app.ams.api.lecture.get

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetLectureHandler
{
    companion object
    {
        fun getLectures(context: Context): ApiResponse = Api.call(path = "/lecture/get", context = context)

        fun ApiResponse.asGetLectureResponse(): GetLectureResponse = Gson().fromJson(this.json, GetLectureResponse::class.java)
    }
}