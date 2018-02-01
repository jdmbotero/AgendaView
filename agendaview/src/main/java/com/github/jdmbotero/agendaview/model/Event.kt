package com.github.jdmbotero.agendaview.model

import android.graphics.Color
import android.view.Gravity
import java.util.*

class Event {

    constructor(name: String = "",
                description: String = "",
                startDate: Calendar,
                timeInMinutes: Int = 30,
                color: Int = Color.parseColor("#474a4f"),
                textColor: Int = Color.parseColor("#ececec")) {
        this.name = name
        this.description = description
        this.startDate = startDate
        this.timeInMinutes = timeInMinutes
        this.color = color
        this.textColor = textColor

        calculateEndDate()
        calculateRangeDate()
    }

    var name: String = ""
    var description: String = ""
    var startDate: Calendar
        set(value) {
            field = value
            calculateEndDate()
            calculateRangeDate()
        }
    var timeInMinutes: Int = 0
        set(value) {
            field = value
            calculateEndDate()
            calculateRangeDate()
        }
    var color: Int = Color.parseColor("#474a4f")
    var textColor: Int = Color.parseColor("#ececec")
    var textGravity: Int = Gravity.START

    lateinit var endDate: Calendar
    lateinit var startDateRange: Calendar
    lateinit var endDateRange: Calendar

    private fun calculateEndDate() {
        endDate = Calendar.getInstance()
        endDate.time = startDate.time
        endDate.add(Calendar.MINUTE, timeInMinutes)
    }

    private fun calculateRangeDate() {
        startDateRange = Calendar.getInstance()
        startDateRange.time = startDate.time
        startDateRange.add(Calendar.MINUTE, 1)

        endDateRange = Calendar.getInstance()
        endDateRange.time = endDate.time
        endDateRange.add(Calendar.MINUTE, -1)
    }
}