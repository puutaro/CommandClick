package com.puutaro.commandclick.util.datetime

import android.icu.util.Calendar

object DateTimeConverter {
    fun convert(
        datetime: String
    ): String {
        val dateTimeList = datetime.split("T")
        val yearMonthDay = dateTimeList.firstOrNull()?.split('-')
            ?: return System.currentTimeMillis().toString()
        val hourMinutes = dateTimeList.getOrNull(1)?.split(':')
            ?: return System.currentTimeMillis().toString()
        val calender = java.util.Calendar.getInstance()
        val year = yearMonthDay.getOrNull(0)?.toInt()
            ?: calender.get(java.util.Calendar.YEAR)
        val month = yearMonthDay.getOrNull(1)?.toInt()
            ?: calender.get(java.util.Calendar.MONTH)
        val day = yearMonthDay.getOrNull(2)?.toInt()
            ?: calender.get(java.util.Calendar.DATE)
        val hour = hourMinutes.firstOrNull()?.toInt()
            ?: calender.get(java.util.Calendar.HOUR)
        val minute = hourMinutes.getOrNull(1)?.toInt()
            ?: calender.get(java.util.Calendar.MINUTE)
        val calenderDatetime: Calendar = Calendar.getInstance()
        calenderDatetime.set(year, month, day, hour, minute)
        val miliTimeCon = calenderDatetime.timeInMillis.toString()
        return miliTimeCon
    }
}