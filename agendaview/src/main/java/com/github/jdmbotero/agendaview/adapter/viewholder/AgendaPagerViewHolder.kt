package com.github.jdmbotero.agendaview.adapter.viewholder

import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
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
import com.github.jdmbotero.agendaview.adapter.AgendaListAdapter
import com.github.jdmbotero.agendaview.model.Day
import com.github.jdmbotero.agendaview.model.Event
import com.github.jdmbotero.agendaview.util.DateManager
import com.github.jdmbotero.agendaview.util.Utils
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.subjects.PublishSubject
import java.util.*

class AgendaPagerViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    private var scrollView: NestedScrollView = view.findViewById(R.id.scrollView)
    private var textCurrentDate: TextView = view.findViewById(R.id.textCurrentDate)
    private var contentHours: LinearLayout = view.findViewById(R.id.contentHours)
    private var contentLines: LinearLayout = view.findViewById(R.id.contentLines)
    private var contentNewEvent: LinearLayout = view.findViewById(R.id.contentNewEvent)
    private var listEvents: RecyclerView = view.findViewById(R.id.listEvents)

    private var contentCurrentDate: LinearLayout = view.findViewById(R.id.contentCurrentDate)

    fun bind(day: Day, observable: PublishSubject<Calendar>) {
        scrollView.scrollTo(0, 0)
        textCurrentDate.text = DateManager.getFormatDate(day.date)

        initHours(day, observable)
        initRecyclerView(day)
        initCurrentView(day)
    }

    private fun initHours(day: Day, observable: PublishSubject<Calendar>) {
        try {
            contentHours.removeAllViews()
            contentLines.removeAllViews()
            for (i in 0..24) {
                val textHour = getTextViewHour(i, String.format("%02d", if (i == 24) 0 else i) + ":00")
                contentHours.addView(textHour)

                val buttonHour = getButtonHour()
                if (i == 24) {
                    buttonHour.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, view.resources.displayMetrics).toInt()
                } else {
                    RxView.touches(buttonHour).subscribe { motionEvent ->
                        if (motionEvent.action == MotionEvent.ACTION_UP) {
                            val hours = i
                            val minutes = (motionEvent.y / view.context.resources.getDimension(R.dimen.agenda_view_hour_height)) * 60

                            val date = Calendar.getInstance()
                            date.time = day.date.time

                            date.set(Calendar.HOUR_OF_DAY, hours)
                            date.set(Calendar.MINUTE, minutes.toInt())

                            if (AgendaView.showNewEventInClick) addNewEvent(day, date)
                            observable.onNext(date)
                        }
                    }
                }

                contentLines.addView(buttonHour)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTextViewHour(hour: Int, text: String): TextView {
        val textView = TextView(view.context)

        try {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    view.context.resources.getDimension(R.dimen.agenda_view_hour_height).toInt())
            textView.layoutParams = params

            textView.text = text
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, view.resources.displayMetrics))
            textView.gravity = Gravity.END

            val date = Calendar.getInstance()
            date.set(Calendar.HOUR_OF_DAY, hour)
            date.set(Calendar.MINUTE, 0)

            val startDate = Calendar.getInstance()
            startDate.add(Calendar.MINUTE, -10)

            val endDate = Calendar.getInstance()
            endDate.add(Calendar.MINUTE, 10)

            if (date in startDate..endDate) {
                textView.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return textView
    }

    private fun getButtonHour(): View {
        val view = View(view.context)

        try {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, view.context.resources.getDimension(R.dimen.agenda_view_hour_height).toInt())
            view.layoutParams = params

            view.background = view.resources.getDrawable(R.drawable.agenda_view_bg_button_hour)
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

            val adapter = AgendaListAdapter(day.events)
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
                val topMargin = (view.context.resources.getDimension(R.dimen.agenda_view_hour_height) * minutes / 60).toInt()

                val params = contentCurrentDate.layoutParams as RelativeLayout.LayoutParams
                params.setMargins(0, topMargin, 0, 0)
                contentCurrentDate.layoutParams = params

                (contentCurrentDate.getChildAt(0) as TextView).text = DateManager.getFormatDate(currentDate, "HH:mm")
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

    private fun addNewEvent(day: Day, date: Calendar) {
        try {
            var minutes = 0
            when (date.get(Calendar.MINUTE)) {
                in 0..8 -> minutes = 0
                in 8..23 -> minutes = 15
                in 23..38 -> minutes = 30
                in 38..53 -> minutes = 45
                in 53..60 -> minutes = 60
            }
            date.set(Calendar.MINUTE, minutes)

            val newEvent = Event(
                    "New Event", "", date,
                    AgendaView.newEventTimeInMinutes,
                    AgendaView.newEventColor,
                    AgendaView.newEventTextColor)

            val events = day.events.filter { event ->
                (newEvent.startDate in event.startDate..event.endDate) || (newEvent.endDate in event.startDate..event.endDate)
            }

            if (events.isEmpty()) {
                AgendaListViewHolder.setUpEventView(contentNewEvent, newEvent, null)
                AgendaListViewHolder.setUpEventStyle(contentNewEvent.getChildAt(1), newEvent)
                (contentNewEvent.getChildAt(0) as TextView).text = if (minutes in 15..45) ":" + minutes.toString() else ""
                contentNewEvent.visibility = View.VISIBLE

                RxView.clicks(contentNewEvent).subscribe {
                    Log.e("Create event", "Create event")
                }
            } else {
                contentNewEvent.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}