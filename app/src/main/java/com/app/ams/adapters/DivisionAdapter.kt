package com.app.ams.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.app.ams.api.division.get.models.Division

class DivisionAdapter(context: Context, private val divisionList: ArrayList<Division>) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line)
{
    override fun getCount(): Int
    {
        return divisionList.size
    }

    override fun getItem(position: Int): String
    {
        return divisionList[position].division
    }

    override fun getItemId(position: Int): Long
    {
        return divisionList[position].id.toLong()
    }

    fun getDivisionId(position: Int): Int
    {
        return divisionList[position].id
    }
}