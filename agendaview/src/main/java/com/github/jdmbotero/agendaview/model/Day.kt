package com.github.jdmbotero.agendaview.model

import java.util.*

data class Day(
        var date: Calendar,
        var isToday: Boolean = false,
        var isSelected: Boolean = false,
        var daysPagerPos: Int = 0,
        var daysPos: Int = 0,
        var agendaPagerPos: Int = 0,
        var events: ArrayList<Event>? = null
)