package com.github.jdmbotero.agendaview.model

import java.util.*

data class Day(
        var date: Calendar,
        var events: ArrayList<Event>? = null
)