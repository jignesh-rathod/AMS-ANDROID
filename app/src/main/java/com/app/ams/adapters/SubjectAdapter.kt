package com.app.ams.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.app.ams.api.subject.get.models.Subject

class SubjectAdapter(context: Context, private val subjectList: ArrayList<Subject>) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line)
{
    override fun getCount(): Int
    {
        return subjectList.size
    }

    override fun getItem(position: Int): String
    {
        return subjectList[position].name
    }

    override fun getItemId(position: Int): Long
    {
        return subjectList[position].id.toLong()
    }

    fun getSubjectId(position: Int): Int
    {
        return subjectList[position].id
    }
}
