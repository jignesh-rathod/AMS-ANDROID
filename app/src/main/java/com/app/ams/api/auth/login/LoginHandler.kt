package com.app.ams.api.auth.login

import com.google.gson.Gson

import com.app.ams.api.Api
import com.app.ams.api.ApiResponse

class LoginHandler
{
    companion object
    {
        fun login(loginRequest: LoginRequest): ApiResponse = Api.call(path = "/auth/login", requestMethod = "POST", data = Gson().toJson(loginRequest), sendToken = false)

        fun ApiResponse.asLoginResponse(): LoginResponse = Gson().fromJson(this.json, LoginResponse::class.java)
    }
}