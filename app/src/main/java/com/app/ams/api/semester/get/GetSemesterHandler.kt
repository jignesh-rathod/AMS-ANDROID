package com.app.ams.api.semester.get

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetSemesterHandler
{
    companion object
    {
        fun getSemesters(context: Context, batchId: Int): ApiResponse = Api.call(path = "/semester/get?batchid=$batchId", context = context)

        fun ApiResponse.asGetSemesterResponse(): GetSemesterResponse = Gson().fromJson(this.json, GetSemesterResponse::class.java)
    }
}