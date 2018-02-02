package com.github.jdmbotero.agendaview.adapter.viewholder

import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.github.jdmbotero.agendaview.AgendaView
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.adapter.DayListAdapter
import com.github.jdmbotero.agendaview.model.Day
import com.github.jdmbotero.agendaview.model.Event
import com.github.jdmbotero.agendaview.util.DateManager
import com.github.jdmbotero.agendaview.util.Utils
import com.jakewharton.rxbinding2.view.RxView
import java.util.*

class DayPagerViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    private var scrollView: NestedScrollView = view.findViewById(R.id.scrollView)
    private var textCurrentDate: TextView = view.findViewById(R.id.textCurrentDate)
    private var contentHours: LinearLayout = view.findViewById(R.id.contentHours)
    private var contentLines: LinearLayout = view.findViewById(R.id.contentLines)
    private var contentButtons: LinearLayout = view.findViewById(R.id.contentButtons)
    private var contentNewEvent: LinearLayout = view.findViewById(R.id.contentNewEvent)
    private var listEvents: RecyclerView = view.findViewById(R.id.listEvents)

    private var contentCurrentDate: LinearLayout = view.findViewById(R.id.contentCurrentDate)

    fun bind(day: Day) {
        scrollView.scrollTo(0, 0)
        textCurrentDate.text = DateManager.getFormatDate(day.date)
        textCurrentDate.setTextColor(AgendaView.dayCurrentTextColor)
        contentNewEvent.visibility = View.GONE

        initHours(day)
        initRecyclerView(day)
        initCurrentView(day)

        if (AgendaView.showNewEvent && AgendaView.newEvent != null
                && DateManager.isSameDay(AgendaView.newEvent!!.startDate, day.date)) {
            addNewEvent(day, AgendaView.newEvent!!, true)
        }
    }

    private fun initHours(day: Day) {
        try {
            contentHours.removeAllViews()
            contentButtons.removeAllViews()
            contentLines.removeAllViews()
            for (i in 0..24) {
                val textHour = getTextViewHour(day, i, String.format("%02d", if (i == 24) 0 else i) + ":00")
                contentHours.addView(textHour)
                contentLines.addView(getLine())

                if (i < 24) {
                    val buttonHour = getButtonHour()
                    contentButtons.addView(buttonHour)

                    RxView.touches(buttonHour).subscribe { motionEvent ->
                        if (motionEvent.action == MotionEvent.ACTION_UP) {
                            val hours = i
                            var minutes = (motionEvent.y / AgendaView.hourHeight) * 60

                            when (minutes) {
                                in 0..8 -> minutes = 0f
                                in 8..23 -> minutes = 15f
                                in 23..38 -> minutes = 30f
                                in 38..53 -> minutes = 45f
                                in 53..60 -> minutes = 60f
                            }

                            val date = Calendar.getInstance()
                            date.time = day.date.time
                            date.set(Calendar.HOUR_OF_DAY, hours)
                            date.set(Calendar.MINUTE, minutes.toInt())

                            val events = day.events.filter { event ->
                                (date == event.startDate) || (date == event.endDate) || (date in event.startDateRange..event.endDateRange)
                            }

                            if (events.isEmpty()) {
                                if (AgendaView.showNewEventInClick) {
                                    if (AgendaView.allowNewEventPrevNow) addNewEvent(day, date)
                                    else if (date >= Calendar.getInstance()) addNewEvent(day, date)
                                }
                                AgendaView.onHourClickListener?.invoke(date)
                            } else {
                                AgendaView.onEventClickListener?.invoke(events.first())
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTextViewHour(day: Day, hour: Int, text: String): TextView {
        val textView = TextView(view.context)

        try {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AgendaView.hourHeight.toInt())
            textView.layoutParams = params

            textView.text = text
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, view.resources.displayMetrics))
            textView.gravity = Gravity.END
            textView.setTextColor(AgendaView.hourTextColor)

            val date = Calendar.getInstance()
            date.set(Calendar.HOUR_OF_DAY, hour)
            date.set(Calendar.MINUTE, 0)

            val startDate = Calendar.getInstance()
            startDate.add(Calendar.MINUTE, -10)

            val endDate = Calendar.getInstance()
            endDate.add(Calendar.MINUTE, 10)

            if (day.isToday && date in startDate..endDate) {
                textView.visibility = View.INVISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return textView
    }

    private fun getLine(): View {
        val view = View(view.context)

        try {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AgendaView.hourHeight.toInt())
            view.layoutParams = params

            view.background = view.resources.getDrawable(R.drawable.agenda_view_bg_button_hour)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }

    private fun getButtonHour(): View {
        val view = View(view.context)

        try {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AgendaView.hourHeight.toInt())
            view.layoutParams = params
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }

    private fun initRecyclerView(day: Day) {
        try {
            listEvents.isNestedScrollingEnabled = false

            listEvents.setHasFixedSize(true)
            listEvents.layoutManager = LinearLayoutManager(view.context)

            val adapter = DayListAdapter(day.events)
            listEvents.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initCurrentView(day: Day) {
        try {
            val currentDate = Calendar.getInstance()
            if (day.date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
                    && day.date.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
                    && day.date.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)) {

                val date = Calendar.getInstance()
                date.set(Calendar.HOUR_OF_DAY, 0)
                date.set(Calendar.MINUTE, 0)
                date.set(Calendar.SECOND, 0)

                val minutes = (currentDate.timeInMillis - date.timeInMillis) / 60000
                val topMargin = (AgendaView.hourHeight * minutes / 60).toInt()

                val params = contentCurrentDate.layoutParams as RelativeLayout.LayoutParams
                params.setMargins(0, topMargin, 0, 0)
                contentCurrentDate.layoutParams = params

                (contentCurrentDate.getChildAt(0) as TextView).text = DateManager.getFormatDate(currentDate, "HH:mm")
                (contentCurrentDate.getChildAt(0) as TextView).setTextColor(AgendaView.hourCurrentColor)
                contentCurrentDate.getChildAt(1).setBackgroundColor(AgendaView.hourCurrentColor)

                contentCurrentDate.visibility = View.VISIBLE

                val screenSize = Utils.getScreenSize(view.context)
                ViewPropertyObjectAnimator.animate(scrollView).scrollY(if (topMargin >= (screenSize[1] * 0.3)) (topMargin - (screenSize[1] * 0.3)).toInt() else 0).start()
            } else {
                contentCurrentDate.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNewEvent(day: Day, date: Calendar, goToEvent: Boolean = false) {
        try {
            val newEvent = Event(
                    AgendaView.newEventText!!, "", date,
                    AgendaView.newEventTimeInMinutes,
                    AgendaView.newEventColor,
                    AgendaView.newEventTextColor)

            addNewEvent(day, newEvent, goToEvent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNewEvent(day: Day, newEvent: Event, goToEvent: Boolean = false) {
        try {
            val dayEndDate = Calendar.getInstance()
            dayEndDate.time = day.date.time
            dayEndDate.set(Calendar.HOUR_OF_DAY, 24)
            dayEndDate.set(Calendar.MINUTE, 0)
            dayEndDate.set(Calendar.SECOND, 0)

            if (newEvent.endDate <= dayEndDate) {
                val events = day.events.filter { event ->
                    (newEvent.startDate == event.startDate) ||
                            (newEvent.endDate == event.endDate) ||
                            (event.startDate in newEvent.startDateRange..newEvent.endDateRange) ||
                            (event.endDate in newEvent.startDateRange..newEvent.endDateRange) ||
                            (newEvent.startDate in event.startDateRange..event.endDateRange) ||
                            (newEvent.endDate in event.startDateRange..event.endDateRange)
                }

                if (events.isEmpty()) {
                    AgendaView.newEvent = newEvent

                    var topMargin = DayListViewHolder.setUpEventView(contentNewEvent, newEvent, null)
                    if (topMargin == null) topMargin = 0

                    DayListViewHolder.setUpEventStyle(contentNewEvent.getChildAt(1), newEvent)
                    (contentNewEvent.getChildAt(0) as TextView).text =
                            if (newEvent.startDate.get(Calendar.MINUTE) in 15..45) ":" + newEvent.startDate.get(Calendar.MINUTE).toString() else ""

                    contentNewEvent.visibility = View.VISIBLE

                    RxView.clicks(contentNewEvent).subscribe {
                        AgendaView.onNewEventClickListener?.invoke(newEvent)
                    }

                    if (goToEvent) {
                        val screenSize = Utils.getScreenSize(view.context)
                        ViewPropertyObjectAnimator.animate(scrollView).scrollY(if (topMargin >= (screenSize[1] * 0.3)) (topMargin - (screenSize[1] * 0.3)).toInt() else 0).start()
                    }
                } else {
                    contentNewEvent.visibility = View.GONE
                }
            } else {
                contentNewEvent.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showHour(hour: Int) {
        ViewPropertyObjectAnimator.animate(scrollView).scrollY((hour * AgendaView.hourHeight).toInt()).start()
    }
}