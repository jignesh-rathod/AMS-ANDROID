package com.app.ams.api.subject.get

import com.app.ams.api.subject.get.models.Subject

data class GetSubjectResponse(val totalResults: Int, val subjects: ArrayList<Subject>)
