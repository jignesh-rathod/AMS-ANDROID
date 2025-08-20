package com.app.ams.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.app.ams.api.lecture.get.models.Lecture

class LectureAdapter(context: Context, private val lectureList: ArrayList<Lecture>): ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line)
{
    override fun getCount(): Int {
        return lectureList.size
    }

    override fun getItem(position: Int): String
    {
        return lectureList[position].time
    }

    override fun getItemId(position: Int): Long {
        return lectureList[position].id.toLong()
    }

    fun getLectureId(position: Int): Int {
        return lectureList[position].id
    }
}