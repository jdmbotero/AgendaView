package com.github.jdmbotero.agendaview.util

import java.text.SimpleDateFormat
import java.util.*

object DateManager {

    private fun getCalendar(year: Int, monthOfYear: Int, dayOfMonth: Int): Calendar {
        val calendarDate = Calendar.getInstance()
        if (year > 0 && monthOfYear > -1 && dayOfMonth > 0) {
            calendarDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
        }

        return calendarDate
    }

    fun getCalendar(date: String): Calendar {
        return getCalendar(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    }

    private fun getCalendar(date: String?, formatString: String): Calendar {
        val calendarDate = Calendar.getInstance()

        try {
            if (date != null && "" != date) {
                val format = SimpleDateFormat(formatString, Locale.US)
                calendarDate.time = format.parse(date)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return calendarDate
    }

    fun getFormatDate(calendarDate: Calendar, formatNeeded: String? = null): String {
        var formatNeeded = formatNeeded
        if (formatNeeded == null) formatNeeded = "EEEE, MMMM d, yyyy"

        val format = SimpleDateFormat(formatNeeded, Locale.US)
        return format.format(calendarDate.time)
    }
}