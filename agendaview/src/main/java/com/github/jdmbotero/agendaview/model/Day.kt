package com.github.jdmbotero.agendaview.model

import java.util.*
import kotlin.collections.ArrayList

data class Day(
        var date: Calendar,
        var isToday: Boolean = false,
        var isSelected: Boolean = false,
        var dayPosition: Int = 0,
        var events: kotlin.collections.ArrayList<Event> = ArrayList()
)