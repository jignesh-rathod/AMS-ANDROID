package com.app.ams.api.faculty.get

import com.app.ams.api.faculty.get.models.Faculty

data class GetFacultyResponse(val totalResults: Int, val faculties: ArrayList<Faculty>)
