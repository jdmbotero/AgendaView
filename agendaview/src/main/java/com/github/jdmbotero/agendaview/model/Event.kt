package com.github.jdmbotero.agendaview.model

import java.util.*

data class Event(
        var name: String = "Event",
        var description: String = "",
        var startDate: Calendar,
        var endDate: Calendar
)