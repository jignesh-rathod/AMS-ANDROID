package com.app.ams.api.division.get

import com.app.ams.api.division.get.models.Division

data class GetDivisionResponse(val totalResults: Int, val divisions: ArrayList<Division>)
