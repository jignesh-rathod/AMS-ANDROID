package com.app.ams

class Constant
{
    companion object
    {
        private const val API_HOST: String = "10.10.10.204"
        private const val API_PORT: Int = 8080
        const val API_BASE_URL: String = "http://$API_HOST:$API_PORT/ams/api"

        private const val UPLOADS_HOST: String = "10.10.10.204"
        private const val UPLOADS_PORT: Int = 80
        const val UPLOADS_BASE_URL: String = "http://$UPLOADS_HOST:$UPLOADS_PORT/ams/uploads"

        const val USERTYPE_ADMIN: String = "Admin"
        const val USERTYPE_FACULTY: String = "Faculty"
        const val USERTYPE_STUDENT: String = "Student"
    }
}