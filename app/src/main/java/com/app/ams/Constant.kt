package com.app.ams

class Constant
{
    companion object
    {
        private const val API_HOST: String = "192.168.1.3"
        private const val API_PORT: Int = 8080
        const val API_BASE_URL: String = "http://$API_HOST:$API_PORT/ams/api"
    }
}