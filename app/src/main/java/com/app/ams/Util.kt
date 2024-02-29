package com.app.ams

import android.content.Context
import android.util.DisplayMetrics
import com.app.ams.models.DateDetail
import java.util.*

class Util
{
    companion object
    {
        fun getCurrentDate(): DateDetail
        {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

            return DateDetail(day, month, year)
        }

        fun isToday(date: String): Boolean
        {
            val currentDate = getCurrentDate()
            val temp = date.split("-")
            val attendanceDate = DateDetail(
                day = temp[2].toInt(),
                month = temp[1].toInt() - 1,
                year = temp[0].toInt()
            )

            return currentDate == attendanceDate
        }

        fun dpToPx(context: Context, dp: Int): Int
        {
            val displayMetrics = context.resources.displayMetrics
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
        }
    }
}