package com.github.jdmbotero.agendaview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.github.jdmbotero.agendaview.adapter.AgendaPagerAdapter
import com.github.jdmbotero.agendaview.adapter.DaysPagerAdapter
import com.github.jdmbotero.agendaview.model.Day
import com.github.jdmbotero.agendaview.model.Week
import kotlinx.android.synthetic.main.view_agenda.view.*
import java.util.*
import kotlin.collections.ArrayList

class AgendaView : FrameLayout {

    private val LOG_TAG = AgendaView::class.java.simpleName

    var weeksCount: Int = 50
        set(value) {
            field = value
            daysCount = (weeksCount * 7) - 1
        }
    var daysCount: Int = (weeksCount * 7) - 1
        set(value) {
            field = value
            initDays()
        }

    var startDate: Calendar = Calendar.getInstance()
    var currentDate: Calendar = Calendar.getInstance()

    private var days = ArrayList<Day>()
    private var weeks = ArrayList<Week>()

    private var daysPagerPos: Int = 0
    private var agendaPagerPos: Int = 0

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AgendaView)

            typedArray.recycle()
        }

        LayoutInflater.from(context).inflate(R.layout.view_agenda, this, true)

        startDate.add(Calendar.DAY_OF_YEAR, -7)
        startDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        initDays()
    }

    private fun initDays() {
        days.clear()
        weeks.clear()
        var weekPosition = -1

        for (i in 0..daysCount) {
            val date = Calendar.getInstance()

            date.time = startDate.time
            date.add(Calendar.DAY_OF_YEAR, i)

            days.add(Day(date))

            if (weeks.size == 0 || weeks[weekPosition].days.size >= 7) {
                weeks.add(Week(ArrayList()))
                weekPosition++
            }
            weeks[weekPosition].days.add(Day(date))

            if (date.time == currentDate.time) {
                daysPagerPos = weekPosition
                agendaPagerPos = i
            }
        }

        initDaysPager()
        initAgendaPager()
    }

    private fun initDaysPager() {
        val adapter = DaysPagerAdapter(weeks)
        daysPager.adapter = adapter

        daysPager.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        PagerSnapHelper().attachToRecyclerView(daysPager)

        daysPager.scrollToPosition(daysPagerPos)
    }

    private fun initAgendaPager() {
        val adapter = AgendaPagerAdapter(days)
        agendaPager.adapter = adapter

        agendaPager.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        PagerSnapHelper().attachToRecyclerView(agendaPager)

        agendaPager.scrollToPosition(agendaPagerPos)
    }

}