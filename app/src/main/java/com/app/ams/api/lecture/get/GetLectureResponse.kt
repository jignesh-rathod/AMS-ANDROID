package com.app.ams.api.lecture.get

import com.app.ams.api.lecture.get.models.Lecture

data class GetLectureResponse(val totalResults: Int, val lectures: ArrayList<Lecture>)
