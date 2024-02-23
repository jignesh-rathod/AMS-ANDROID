package com.app.ams.api.semester.get

import com.app.ams.api.semester.get.models.Semester

data class GetSemesterResponse(val totalResults: Int, val semesters: ArrayList<Semester>)
