package com.app.ams

class Constant
{
    companion object
    {
        private const val API_HOST: String = "192.168.43.199" // FIXME: Change This IP With Your API Server IP
        private const val API_PORT: Int = 8080 // FIXME: Change This Port With Your API Server Port
        const val API_BASE_URL: String = "http://$API_HOST:$API_PORT/ams/api"
    }
}