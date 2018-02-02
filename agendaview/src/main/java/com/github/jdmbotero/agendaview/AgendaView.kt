package com.github.jdmbotero.agendaview

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import com.github.jdmbotero.agendaview.adapter.DayPagerAdapter
import com.github.jdmbotero.agendaview.adapter.WeekPagerAdapter
import com.github.jdmbotero.agendaview.adapter.viewholder.DayPagerViewHolder
import com.github.jdmbotero.agendaview.model.Day
import com.github.jdmbotero.agendaview.model.Event
import com.github.jdmbotero.agendaview.model.Week
import com.github.jdmbotero.agendaview.util.DateManager
import kotlinx.android.synthetic.main.view_agenda.view.*
import java.util.*
import kotlin.collections.ArrayList


class AgendaView : FrameLayout {

    private val recyclerViewDisabler = RecyclerViewDisabler()
    private var isFinishInflater: Boolean = false

    private var days = ArrayList<Day>()
    private var weeks = ArrayList<Week>()

    var weekPagerPos: Int = 0
    var dayPagerPos: Int = 0

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

        var newEvent: Event? = null
        var showNewEvent: Boolean = false

        var showNewEventInClick: Boolean = true
        var newEventTimeInMinutes: Int = 60
        var newEventColor: Int = 0
        var newEventText: String? = "New Event"
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

            newEventText = typedArray.getString(R.styleable.AgendaView_newEventText)
            if (newEventText == null)
                newEventText = context.resources.getString(R.string.agenda_view_new_event_text)

            newEventTextColor = typedArray.getInt(R.styleable.AgendaView_newEventTextColor,
                    ContextCompat.getColor(context, R.color.colorEventText))

            allowNewEventPrevNow = typedArray.getBoolean(R.styleable.AgendaView_allowNewEventPrevNow, true)

            typedArray.recycle()
        }

        startDate = Calendar.getInstance()

        LayoutInflater.from(context).inflate(R.layout.view_agenda, this, true)
        this.setBackgroundColor(backgroundColor)

        Log.e("AgendaView", "init")
        Log.e("weekPagerPos", weekPagerPos.toString())
        Log.e("dayPagerPos", dayPagerPos.toString())
        Log.e("weekDayPos", days[dayPagerPos].weekDayPos.toString())
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        isFinishInflater = true
        initDays()

        Log.e("finish Inflate", "init")
        Log.e("weekPagerPos", weekPagerPos.toString())
        Log.e("dayPagerPos", dayPagerPos.toString())
        Log.e("weekDayPos", days[dayPagerPos].weekDayPos.toString())
    }

    inner class RecyclerViewDisabler : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }
    }


    private fun initDays() {
        try {
            days.clear()
            weeks.clear()
            var weekPagerPos = -1

            for (i in 0..numberOfDays) {
                if (weeks.size == 0 || weeks[weekPagerPos].days.size >= 7) {
                    weeks.add(Week(ArrayList()))
                    weekPagerPos++
                }

                val date = Calendar.getInstance()
                date.time = startDate.time
                date.add(Calendar.DAY_OF_YEAR, i)

                val day = Day(date, false, false, weekPagerPos, weeks[weekPagerPos].days.size, i)

                if (DateManager.isSameDay(date, currentDate)) {
                    day.isToday = true
                    day.isSelected = true
                    this.weekPagerPos = weekPagerPos
                    this.dayPagerPos = i
                }

                setUpEventsToDay(day)

                days.add(day)
                weeks[weekPagerPos].days.add(day)
            }

            initWeekPager()
            initDayPager()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpAllEvents() {
        Collections.sort(events) { o1, o2 -> o1.startDate.compareTo(o2.startDate) }
        days.forEach { day ->
            setUpEventsToDay(day)
        }

        (dayPager.adapter as DayPagerAdapter).items = days
    }

    private fun setUpEventsToDay(day: Day) {
        day.events.clear()
        day.events.addAll(events.filter { event ->
            DateManager.isSameDay(day.date, event.startDate)
        })
    }

    private fun initWeekPager() {
        try {
            weekPager.setHasFixedSize(true)
            weekPager.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val adapter = WeekPagerAdapter(weeks)
            weekPager.adapter = adapter

            val pagerSnapHelper = object : PagerSnapHelper() {
                override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
                    val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
                    if (weekPagerPos != position) changeDaysPosition(position)
                    return position
                }
            }
            pagerSnapHelper.attachToRecyclerView(weekPager)
            weekPager.scrollToPosition(weekPagerPos)

            weekPager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    when (newState) {
                        RecyclerView.SCROLL_STATE_DRAGGING -> dayPager.addOnItemTouchListener(recyclerViewDisabler)
                        else -> dayPager.removeOnItemTouchListener(recyclerViewDisabler)
                    }
                }
            })

            adapter.observable.subscribe { day ->
                dayPager.scrollToPosition(day.dayPagerPos)
                changeAgendaPosition(day.dayPagerPos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initDayPager() {
        try {
            dayPager.setHasFixedSize(true)
            dayPager.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val adapter = DayPagerAdapter(days)
            dayPager.adapter = adapter

            val pagerSnapHelper = object : PagerSnapHelper() {
                override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
                    val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
                    if (dayPagerPos != position) changeAgendaPosition(position)
                    return position
                }
            }
            pagerSnapHelper.attachToRecyclerView(dayPager)
            dayPager.scrollToPosition(dayPagerPos)

            dayPager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    when (newState) {
                        RecyclerView.SCROLL_STATE_DRAGGING -> weekPager.addOnItemTouchListener(recyclerViewDisabler)
                        else -> weekPager.removeOnItemTouchListener(recyclerViewDisabler)
                    }
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changeAgendaPosition(position: Int) {
        try {
            (weekPager.adapter as WeekPagerAdapter).items[weekPagerPos].days[days[dayPagerPos].weekDayPos].isSelected = false
            weekPager.adapter.notifyItemChanged(weekPagerPos)
            dayPagerPos = position

            val day = days[dayPagerPos]
            if (day.weekPagerPos != weekPagerPos) {
                weekPagerPos = day.weekPagerPos
                weekPager.smoothScrollToPosition(weekPagerPos)
            }

            (weekPager.adapter as WeekPagerAdapter).items[weekPagerPos].days[days[dayPagerPos].weekDayPos].isSelected = true
            weekPager.adapter.notifyItemChanged(weekPagerPos)

            onDayChangeListener?.invoke(days[dayPagerPos])
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changeDaysPosition(position: Int) {
        try {
            (weekPager.adapter as WeekPagerAdapter).items[weekPagerPos].days[days[dayPagerPos].weekDayPos].isSelected = false
            weekPager.adapter.notifyItemChanged(weekPagerPos)
            weekPagerPos = position

            val day = weeks[weekPagerPos].days[days[dayPagerPos].weekDayPos]
            if (day.dayPagerPos != dayPagerPos) {
                dayPagerPos = day.dayPagerPos
                dayPager.scrollToPosition(dayPagerPos)
                onDayChangeListener?.invoke(days[dayPagerPos])
            }

            (weekPager.adapter as WeekPagerAdapter).items[weekPagerPos].days[days[dayPagerPos].weekDayPos].isSelected = true
            weekPager.adapter.notifyItemChanged(weekPagerPos)
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

                (dayPager.adapter as DayPagerAdapter).items[day.dayPagerPos].events = day.events
                dayPager.adapter.notifyItemChanged(day.dayPagerPos)
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
        try {
            val day: Day? = days.singleOrNull { day ->
                day.date.get(Calendar.YEAR) == date.get(Calendar.YEAR)
                        && day.date.get(Calendar.MONTH) == date.get(Calendar.MONTH)
                        && day.date.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
            }

            if (day != null) {
                dayPagerPos = day.dayPagerPos
                dayPager.scrollToPosition(dayPagerPos)

                (dayPager.findViewHolderForAdapterPosition(dayPagerPos) as DayPagerViewHolder)
                        .showHour(date.get(Calendar.HOUR_OF_DAY))
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    var setNewEvent
        set(value) {
            newEvent = value
        }
        get() = newEvent

    var setShowNewEvent
        set(value) {
            showNewEvent = value
        }
        get() = showNewEvent


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

    var setNewEventText
        set(value) {
            newEventText = value
        }
        get() = newEventText

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