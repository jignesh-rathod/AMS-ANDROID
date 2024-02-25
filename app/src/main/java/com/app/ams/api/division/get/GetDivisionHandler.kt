package com.app.ams.api.division.get

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetDivisionHandler
{
    companion object
    {
        fun getDivisions(context: Context, semesterId: Int): ApiResponse = Api.call(path = "/division/get?semesterid=$semesterId", context = context)

        fun ApiResponse.asGetDivisionResponse(): GetDivisionResponse = Gson().fromJson(this.json, GetDivisionResponse::class.java)
    }
}