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

    private fun parseDate(calendarDate: Calendar): HashMap<String, String> {
        val date = HashMap<String, String>()

        date["year"] = calendarDate.get(Calendar.YEAR).toString()
        date["monthOfYear"] = calendarDate.get(Calendar.MONTH).toString()
        date["dayOfMonth"] = calendarDate.get(Calendar.DAY_OF_MONTH).toString()

        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        date["format"] = format.format(calendarDate.time)

        val formatISO = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        date["formatServer"] = formatISO.format(calendarDate.time)

        return date
    }

    fun parseDate(year: Int, monthOfYear: Int, dayOfMonth: Int): HashMap<String, String> {
        return parseDate(getCalendar(year, monthOfYear, dayOfMonth))
    }

    fun parseDate(date: String): HashMap<String, String> {
        return parseDate(getCalendar(date))
    }

    fun parseDate(date: String, formatString: String): HashMap<String, String> {
        return parseDate(getCalendar(date, formatString))
    }

    fun getCurrentDate(): HashMap<String, String> {
        return parseDate(0, 0, 0)
    }

    fun getFormatDate(date: String, formatActual: String? = null, formatNeeded: String? = null): String {
        var formatActual = formatActual
        var formatNeeded = formatNeeded
        if (formatActual == null) formatActual = "yyyy-MM-dd"
        if (formatNeeded == null) formatNeeded = "MMM d, yyyy"

        val calendarDate = getCalendar(date, formatActual)
        return getFormatDate(calendarDate, formatNeeded)
    }

    fun getFormatDate(calendarDate: Calendar, formatNeeded: String? = null): String {
        var formatNeeded = formatNeeded
        if (formatNeeded == null) formatNeeded = "EEEE, MMMM d, yyyy"

        val format = SimpleDateFormat(formatNeeded, Locale.US)
        return format.format(calendarDate.time)
    }
}