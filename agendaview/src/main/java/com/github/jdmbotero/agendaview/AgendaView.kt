package com.github.jdmbotero.agendaview

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.github.jdmbotero.agendaview.adapter.AgendaPagerAdapter
import com.github.jdmbotero.agendaview.adapter.DaysPagerAdapter
import com.github.jdmbotero.agendaview.model.Day
import com.github.jdmbotero.agendaview.model.Event
import com.github.jdmbotero.agendaview.model.Week
import com.github.jdmbotero.agendaview.util.DateManager
import kotlinx.android.synthetic.main.view_agenda.view.*
import java.util.*
import kotlin.collections.ArrayList

class AgendaView : FrameLayout {

    private var isFinishInflater: Boolean = false

    private var days = ArrayList<Day>()
    private var weeks = ArrayList<Week>()

    private var daysPagerPos: Int = 0
    private var agendaPagerPos: Int = 0

    var currentDate: Calendar = Calendar.getInstance()

    var firstDay: Int = Calendar.SUNDAY
        set(value) {
            field = value
            startDate.set(Calendar.DAY_OF_WEEK, field)

            if (isFinishInflater) initDays()
        }

    var startDate: Calendar = Calendar.getInstance()
        set(value) {
            value.add(Calendar.DAY_OF_YEAR, -7)
            value.set(Calendar.DAY_OF_WEEK, firstDay)
            value.set(Calendar.HOUR_OF_DAY, 0)
            value.set(Calendar.MINUTE, 0)
            value.set(Calendar.SECOND, 0)
            field = value

            if (isFinishInflater) initDays()
        }

    var numberOfDays: Int = 364
        set(value) {
            field = value - (value % 7) - 1

            if (isFinishInflater) initDays()
        }

    var events = ArrayList<Event>()
        set(value) {
            Collections.sort(value) { o1, o2 -> o1.startDate.compareTo(o2.startDate) }
            field = value

            if (isFinishInflater) setUpAllEvents()
        }

    companion object {
        var showNewEventInClick: Boolean = true
        var newEventTimeInMinutes: Int = 60
        var newEventColor: Int = Color.parseColor("#474a4f")
        var newEventTextColor: Int = Color.parseColor("#ececec")
    }

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

            firstDay = typedArray.getInt(R.styleable.AgendaView_firstDay, Calendar.SUNDAY)
            numberOfDays = typedArray.getInt(R.styleable.AgendaView_numberOfDays, 364)

            showNewEventInClick = typedArray.getBoolean(R.styleable.AgendaView_showNewEventInClick, true)
            newEventTimeInMinutes = typedArray.getInt(R.styleable.AgendaView_newEventTimeInMinutes, 60)
            newEventColor = typedArray.getInt(R.styleable.AgendaView_newEventColor, Color.parseColor("#474a4f"))
            newEventTextColor = typedArray.getInt(R.styleable.AgendaView_newEventTextColor, Color.parseColor("#ececec"))

            typedArray.recycle()
        }

        LayoutInflater.from(context).inflate(R.layout.view_agenda, this, true)

        startDate.add(Calendar.DAY_OF_YEAR, -7)
        startDate.set(Calendar.DAY_OF_WEEK, firstDay)
        startDate.set(Calendar.HOUR_OF_DAY, 0)
        startDate.set(Calendar.MINUTE, 0)
        startDate.set(Calendar.SECOND, 0)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        isFinishInflater = true
        initDays()
    }

    fun addNewEvent(event: Event) {
        events.add(event)
        Collections.sort(events) { o1, o2 -> o1.startDate.compareTo(o2.startDate) }

        val day = days.single { day ->
            day.date.get(Calendar.YEAR) == event.startDate.get(Calendar.YEAR)
                    && day.date.get(Calendar.MONTH) == event.startDate.get(Calendar.MONTH)
                    && day.date.get(Calendar.DAY_OF_MONTH) == event.startDate.get(Calendar.DAY_OF_MONTH)
        }

        setUpEventsToDay(day)
        agendaPager.adapter.notifyItemChanged(day.agendaPagerPos)
    }

    private fun initDays() {
        try {
            days.clear()
            weeks.clear()
            var daysPosition = -1

            for (i in 0..numberOfDays) {
                if (weeks.size == 0 || weeks[daysPosition].days.size >= 7) {
                    weeks.add(Week(ArrayList()))
                    daysPosition++
                }

                val date = Calendar.getInstance()
                date.time = startDate.time
                date.add(Calendar.DAY_OF_YEAR, i)

                val day = Day(date, false, false, daysPosition, weeks[daysPosition].days.size, i)

                if (DateManager.getFormatDate(date, "yyyy-MM-dd") ==
                        DateManager.getFormatDate(currentDate, "yyyy-MM-dd")) {
                    day.isToday = true
                    day.isSelected = true
                    daysPagerPos = daysPosition
                    agendaPagerPos = i
                }

                setUpEventsToDay(day)

                days.add(day)
                weeks[daysPosition].days.add(day)
            }

            initDaysPager()
            initAgendaPager()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpAllEvents() {
        days.forEach { day ->
            setUpEventsToDay(day)
        }

        (agendaPager.adapter as AgendaPagerAdapter).items = days
    }

    private fun setUpEventsToDay(day: Day) {
        day.events.clear()
        day.events.addAll(events.filter { event ->
            day.date.get(Calendar.YEAR) == event.startDate.get(Calendar.YEAR)
                    && day.date.get(Calendar.MONTH) == event.startDate.get(Calendar.MONTH)
                    && day.date.get(Calendar.DAY_OF_MONTH) == event.startDate.get(Calendar.DAY_OF_MONTH)
        })
    }

    private fun initDaysPager() {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAgendaPager() {
        try {
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

            adapter.observable.subscribe { date ->

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changeAgendaPosition(position: Int) {
        try {
            (daysPager.adapter as DaysPagerAdapter).items[daysPagerPos].days[days[agendaPagerPos].daysPos].isSelected = false
            daysPager.adapter.notifyItemChanged(daysPagerPos)
            agendaPagerPos = position

            val day = days[agendaPagerPos]
            if (day.daysPagerPos != daysPagerPos) {
                daysPagerPos = day.daysPagerPos
                daysPager.smoothScrollToPosition(daysPagerPos)
            }

            (daysPager.adapter as DaysPagerAdapter).items[daysPagerPos].days[days[agendaPagerPos].daysPos].isSelected = true
            daysPager.adapter.notifyItemChanged(daysPagerPos)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changeDaysPosition(position: Int) {
        try {
            (daysPager.adapter as DaysPagerAdapter).items[daysPagerPos].days[days[agendaPagerPos].daysPos].isSelected = false
            daysPager.adapter.notifyItemChanged(daysPagerPos)
            daysPagerPos = position

            val day = weeks[daysPagerPos].days[days[agendaPagerPos].daysPos]
            if (day.agendaPagerPos != agendaPagerPos) {
                agendaPagerPos = day.agendaPagerPos
                agendaPager.scrollToPosition(agendaPagerPos)
            }

            (daysPager.adapter as DaysPagerAdapter).items[daysPagerPos].days[days[agendaPagerPos].daysPos].isSelected = true
            daysPager.adapter.notifyItemChanged(daysPagerPos)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}