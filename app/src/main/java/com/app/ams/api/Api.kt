package com.app.ams.api

import android.content.Context

import java.net.URL
import java.net.HttpURLConnection

import com.app.ams.Constant

class Api()
{
    companion object
    {
        fun call(path: String, requestMethod: String = "GET", data: String = "", sendToken: Boolean = true, context: Context? = null): ApiResponse
        {
            val url = Constant.API_BASE_URL + path
            val connection = URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = requestMethod
            connection.doInput = true
            connection.doOutput = true

            if (requestMethod == "GET")
                connection.doOutput = false

            if (sendToken)
            {
                val prefs = context!!.getSharedPreferences("ams", Context.MODE_PRIVATE)
                val token = prefs.getString("AMS_TOKEN", "")

                connection.addRequestProperty("Authorization", "Bearer $token")
            }

            if (data != "" && requestMethod == "POST")
            {
                val writer = connection.outputStream.bufferedWriter()
                writer.write(data)
                writer.close()
            }

            val reader = if (connection.responseCode == HttpURLConnection.HTTP_OK) connection.inputStream.bufferedReader() else connection.errorStream.bufferedReader()
            val response = reader.readText()

            reader.close()
            return ApiResponse(connection.responseCode, response)
        }
    }
}
