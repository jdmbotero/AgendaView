package com.github.jdmbotero.agendaview

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.github.jdmbotero.agendaview.adapter.AgendaPagerAdapter
import com.github.jdmbotero.agendaview.adapter.DaysPagerAdapter
import com.github.jdmbotero.agendaview.adapter.viewholder.AgendaPagerViewHolder
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
        var hourHeight: Float = 0f

        var backgroundColor: Int = 0

        var dayTextColor: Int = 0
        var dayCurrentColor: Int = 0
        var dayCurrentTextColor: Int = 0

        var hourTextColor: Int = 0
        var hourCurrentColor: Int = 0

        var dayBackground: Drawable? = null
        var daySelectedBackground: Drawable? = null

        var showNewEventInClick: Boolean = true
        var newEventTimeInMinutes: Int = 60
        var newEventColor: Int = 0
        var newEventTextColor: Int = 0
        var allowNewEventPrevNow: Boolean = true

        var onHourClickListener: ((Calendar) -> Unit)? = null
        var onEventClickListener: ((Event) -> Unit)? = null
        var onNewEventClickListener: ((Event) -> Unit)? = null
        var onDayChangeListener: ((Day) -> Unit)? = null
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

            hourHeight = typedArray.getDimension(R.styleable.AgendaView_hourHeight,
                    context.resources.getDimension(R.dimen.agenda_view_hour_height))

            backgroundColor = typedArray.getInt(R.styleable.AgendaView_backgroundColor,
                    ContextCompat.getColor(context, R.color.colorBackground))

            dayTextColor = typedArray.getInt(R.styleable.AgendaView_dayTextColor,
                    ContextCompat.getColor(context, R.color.colorTextPrimary))

            dayCurrentTextColor = typedArray.getInt(R.styleable.AgendaView_dayCurrentTextColor,
                    ContextCompat.getColor(context, R.color.colorTextGray))
            dayCurrentColor = typedArray.getInt(R.styleable.AgendaView_dayCurrentColor,
                    ContextCompat.getColor(context, R.color.colorAccent))

            hourTextColor = typedArray.getInt(R.styleable.AgendaView_hourTextColor,
                    ContextCompat.getColor(context, R.color.colorTextPrimary))
            hourCurrentColor = typedArray.getInt(R.styleable.AgendaView_hourCurrentColor,
                    ContextCompat.getColor(context, R.color.colorAccent))

            dayBackground = typedArray.getDrawable(R.styleable.AgendaView_dayBackground)
            if (dayBackground == null)
                dayBackground = ContextCompat.getDrawable(context, R.drawable.agenda_view_bg_day)

            daySelectedBackground = typedArray.getDrawable(R.styleable.AgendaView_daySelectedBackground)
            if (daySelectedBackground == null)
                daySelectedBackground = ContextCompat.getDrawable(context, R.drawable.agenda_view_bg_day_selected)


            showNewEventInClick = typedArray.getBoolean(R.styleable.AgendaView_showNewEventInClick, true)
            newEventTimeInMinutes = typedArray.getInt(R.styleable.AgendaView_newEventTimeInMinutes, 60)

            newEventColor = typedArray.getInt(R.styleable.AgendaView_newEventColor,
                    ContextCompat.getColor(context, R.color.colorEvent))
            newEventTextColor = typedArray.getInt(R.styleable.AgendaView_newEventTextColor,
                    ContextCompat.getColor(context, R.color.colorEventText))

            typedArray.recycle()
        }

        LayoutInflater.from(context).inflate(R.layout.view_agenda, this, true)
        this.setBackgroundColor(backgroundColor)

        startDate.add(Calendar.DAY_OF_YEAR, -7)
        startDate.set(Calendar.DAY_OF_WEEK, firstDay)
        startDate.set(Calendar.HOUR_OF_DAY, 0)
        startDate.set(Calendar.MINUTE, 0)
        startDate.set(Calendar.SECOND, 0)

        initDays()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        isFinishInflater = true
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
        Collections.sort(events) { o1, o2 -> o1.startDate.compareTo(o2.startDate) }
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

            onDayChangeListener?.invoke(days[agendaPagerPos])
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

            onDayChangeListener?.invoke(days[agendaPagerPos])
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


    /**
     * Public Methods
     */

    fun addEvent(newEvent: Event) {
        val day: Day? = days.singleOrNull { day ->
            day.date.get(Calendar.YEAR) == newEvent.startDate.get(Calendar.YEAR)
                    && day.date.get(Calendar.MONTH) == newEvent.startDate.get(Calendar.MONTH)
                    && day.date.get(Calendar.DAY_OF_MONTH) == newEvent.startDate.get(Calendar.DAY_OF_MONTH)
        }


        if (day != null) {
            val eventsFound = day.events.filter { event ->
                (newEvent.startDate == event.startDate) ||
                        (newEvent.endDate == event.endDate) ||
                        (event.startDate in newEvent.startDateRange..newEvent.endDateRange) ||
                        (event.endDate in newEvent.startDateRange..newEvent.endDateRange) ||
                        (newEvent.startDate in event.startDateRange..event.endDateRange) ||
                        (newEvent.endDate in event.startDateRange..event.endDateRange)
            }

            if (eventsFound.isEmpty()) {
                events.add(newEvent)
                day.events.add(newEvent)
                Collections.sort(day.events) { o1, o2 -> o1.startDate.compareTo(o2.startDate) }

                (agendaPager.adapter as AgendaPagerAdapter).items[day.agendaPagerPos].events = day.events
                agendaPager.adapter.notifyItemChanged(day.agendaPagerPos)
            }
        }
    }

    fun setOnHourClickListener(listener: (Calendar) -> Unit) {
        onHourClickListener = listener
    }

    fun setOnEventClickListener(listener: (Event) -> Unit) {
        onEventClickListener = listener
    }

    fun setOnNewEventClickListener(listener: (Event) -> Unit) {
        onNewEventClickListener = listener
    }

    fun setOnDayChangeListener(listener: (Day) -> Unit) {
        onDayChangeListener = listener
    }

    fun showDate(date: Calendar) {
        val day: Day? = days.singleOrNull { day ->
            day.date.get(Calendar.YEAR) == date.get(Calendar.YEAR)
                    && day.date.get(Calendar.MONTH) == date.get(Calendar.MONTH)
                    && day.date.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
        }

        if (day != null) {
            agendaPagerPos = day.agendaPagerPos
            agendaPager.scrollToPosition(agendaPagerPos)

            (agendaPager.findViewHolderForAdapterPosition(agendaPagerPos) as AgendaPagerViewHolder)
                    .showHour(date.get(Calendar.HOUR_OF_DAY))
        }
    }

    var setHourHeight
        set(value) {
            hourHeight = value
        }
        get() = hourHeight

    var setBackgroundColor
        set(value) {
            backgroundColor = value
        }
        get() = backgroundColor

    var setDayTextColor
        set(value) {
            dayTextColor = value
        }
        get() = dayTextColor

    var setDayCurrentColor
        set(value) {
            dayCurrentColor = value
        }
        get() = dayCurrentColor

    var setDayCurrentTextColor
        set(value) {
            dayCurrentTextColor = value
        }
        get() = dayCurrentTextColor

    var setHourTextColor
        set(value) {
            hourTextColor = value
        }
        get() = hourTextColor

    var setHourCurrentColor
        set(value) {
            hourCurrentColor = value
        }
        get() = hourCurrentColor

    var setDayBackground
        set(value) {
            dayBackground = value
        }
        get() = dayBackground

    var setDaySelectedBackground
        set(value) {
            daySelectedBackground = value
        }
        get() = daySelectedBackground

    var setShowNewEventInClick
        set(value) {
            showNewEventInClick = value
        }
        get() = showNewEventInClick

    var setNewEventTimeInMinutes
        set(value) {
            newEventTimeInMinutes = value
        }
        get() = newEventTimeInMinutes

    var setNewEventColor
        set(value) {
            newEventColor = value
        }
        get() = newEventColor

    var setNewEventTextColor
        set(value) {
            newEventTextColor = value
        }
        get() = newEventTextColor

    var setAllowNewEventPrevNow
        set(value) {
            allowNewEventPrevNow = value
        }
        get() = allowNewEventPrevNow
}