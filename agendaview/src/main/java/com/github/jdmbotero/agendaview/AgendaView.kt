package com.github.jdmbotero.agendaview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
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
        var daysPosition = -1

        for (i in 0..daysCount) {
            if (weeks.size == 0 || weeks[daysPosition].days.size >= 7) {
                weeks.add(Week(ArrayList()))
                daysPosition++
            }

            val date = Calendar.getInstance()
            date.time = startDate.time
            date.add(Calendar.DAY_OF_YEAR, i)

            val day = Day(date, false, false, daysPosition, weeks[daysPosition].days.size, i)

            if (date.time == currentDate.time) {
                day.isToday = true
                day.isSelected = true
                daysPagerPos = daysPosition
                agendaPagerPos = i
            }

            days.add(day)
            weeks[daysPosition].days.add(day)
        }

        initDaysPager()
        initAgendaPager()
    }

    private fun initDaysPager() {
        val adapter = DaysPagerAdapter(weeks)
        daysPager.adapter = adapter

        daysPager.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val pagerSnapHelper = object : PagerSnapHelper() {
            override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
                val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
                if (daysPagerPos != position) changeDaysPosition(position)
                return position
            }
        }
        pagerSnapHelper.attachToRecyclerView(daysPager)
        daysPager.scrollToPosition(daysPagerPos)

        adapter.observable.subscribe { day ->
            agendaPager.smoothScrollToPosition(day.agendaPagerPos)
            changeAgendaPosition(day.agendaPagerPos)
        }
    }

    private fun initAgendaPager() {
        val adapter = AgendaPagerAdapter(days)
        agendaPager.adapter = adapter

        agendaPager.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val pagerSnapHelper = object : PagerSnapHelper() {
            override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
                val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
                if (position != agendaPagerPos) changeAgendaPosition(position)
                return position
            }
        }
        pagerSnapHelper.attachToRecyclerView(agendaPager)
        agendaPager.scrollToPosition(agendaPagerPos)
    }

    private fun changeAgendaPosition(position: Int) {
        (daysPager.adapter as DaysPagerAdapter).items[daysPagerPos].days[days[agendaPagerPos].daysPos].isSelected = false
        agendaPagerPos = position

        val day = days[agendaPagerPos]
        if (day.daysPagerPos != daysPagerPos) {
            daysPagerPos = day.daysPagerPos
            daysPager.smoothScrollToPosition(daysPagerPos)
        }

        (daysPager.adapter as DaysPagerAdapter).items[daysPagerPos].days[days[agendaPagerPos].daysPos].isSelected = true
        daysPager.adapter.notifyDataSetChanged()
    }

    private fun changeDaysPosition(position: Int) {
        (daysPager.adapter as DaysPagerAdapter).items[daysPagerPos].days[days[agendaPagerPos].daysPos].isSelected = false
        daysPagerPos = position

        val day = weeks[daysPagerPos].days[days[agendaPagerPos].daysPos]
        if (day.agendaPagerPos != agendaPagerPos) {
            agendaPagerPos = day.agendaPagerPos
            agendaPager.scrollToPosition(agendaPagerPos)
        }

        (daysPager.adapter as DaysPagerAdapter).items[daysPagerPos].days[days[agendaPagerPos].daysPos].isSelected = true
        daysPager.adapter.notifyDataSetChanged()
    }
}