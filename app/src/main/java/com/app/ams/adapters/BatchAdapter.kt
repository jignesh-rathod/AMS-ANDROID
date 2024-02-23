package com.app.ams.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.app.ams.api.batch.get.models.Batch

class BatchAdapter(context: Context, private val batchList: ArrayList<Batch>) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line)
{
    override fun getCount(): Int
    {
        return batchList.size
    }

    override fun getItem(position: Int): String
    {
        return batchList[position].name
    }

    override fun getItemId(position: Int): Long
    {
        return batchList[position].id.toLong()
    }

    fun getBatchId(position: Int): Int
    {
        return batchList[position].id
    }
}