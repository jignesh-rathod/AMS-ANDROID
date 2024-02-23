package com.app.ams.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.app.ams.api.semester.get.models.Semester

class SemesterAdapter(context: Context, private val semesterList: ArrayList<Semester>) : ArrayAdapter<Int>(context, android.R.layout.simple_dropdown_item_1line)
{
    override fun getCount(): Int
    {
        return semesterList.size
    }

    override fun getItem(position: Int): Int
    {
        return semesterList[position].semester
    }

    override fun getItemId(position: Int): Long
    {
        return semesterList[position].id.toLong()
    }

    fun getSemesterId(position: Int): Int
    {
        return semesterList[position].id
    }
}