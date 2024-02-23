package com.app.ams.api.batch.get

import android.content.Context
import com.app.ams.api.Api
import com.app.ams.api.ApiResponse
import com.google.gson.Gson

class GetBatchHandler
{
    companion object
    {
        fun getBatches(context: Context): ApiResponse = Api.call(path = "/batch/get", context = context)

        fun ApiResponse.asGetBatchResponse(): GetBatchResponse = Gson().fromJson(this.json, GetBatchResponse::class.java)
    }
}