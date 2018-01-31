package com.github.jdmbotero.agendaview.model

import android.graphics.Color
import java.util.*

class Event(
        var name: String = "",
        var description: String = "",
        var startDate: Calendar,
        timeInMinutes: Int = 30,
        var color: Int = Color.parseColor("#474a4f"),
        var textColor: Int = Color.parseColor("#ececec")
) {

    init {
        calculateEndDate()
    }

    var timeInMinutes: Int = timeInMinutes
        set(value) {
            field = value
            calculateEndDate()
        }

    lateinit var endDate: Calendar
    private fun calculateEndDate() {
        endDate = Calendar.getInstance()
        endDate.time = startDate.time
        endDate.add(Calendar.MINUTE, timeInMinutes)
    }
}