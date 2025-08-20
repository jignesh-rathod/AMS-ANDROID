package com.app.ams.api.batch.get

import com.app.ams.api.batch.get.models.Batch

data class GetBatchResponse(val totalResults: Int, val batches: ArrayList<Batch>)
