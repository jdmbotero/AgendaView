package com.github.jdmbotero.agendaview.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.model.Event
import java.util.*


class AgendaListViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    fun bind(event: Event, prevEvent: Event?) {
        setUpEventView(view, event, prevEvent)
        setUpEventStyle(view, event)
    }

    companion object {
        fun setUpEventView(view: View, event: Event, prevEvent: Event?) {
            try {
                val date = Calendar.getInstance()
                if (prevEvent != null) {
                    date.time = prevEvent.startDate.time
                    date.add(Calendar.MINUTE, prevEvent.timeInMinutes)
                } else {
                    date.time = event.startDate.time
                    date.set(Calendar.HOUR_OF_DAY, 0)
                    date.set(Calendar.MINUTE, 0)
                    date.set(Calendar.SECOND, 0)
                }

                val minutes = (event.startDate.timeInMillis - date.timeInMillis) / 60000
                val topMargin = (view.context.resources.getDimension(R.dimen.agenda_view_hour_height) * minutes / 60).toInt()

                if (view.layoutParams is RecyclerView.LayoutParams) {
                    val params = view.layoutParams as RecyclerView.LayoutParams
                    params.height = (view.context.resources.getDimension(R.dimen.agenda_view_hour_height) * event.timeInMinutes / 60).toInt()
                    params.setMargins(0, topMargin, 0, 0)
                    view.layoutParams = params
                } else if (view.layoutParams is RelativeLayout.LayoutParams) {
                    val params = view.layoutParams as RelativeLayout.LayoutParams
                    params.height = (view.context.resources.getDimension(R.dimen.agenda_view_hour_height) * event.timeInMinutes / 60).toInt()
                    params.setMargins(0, topMargin + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, view.resources.displayMetrics).toInt(), 0, 0)
                    view.layoutParams = params
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun setUpEventStyle(view: View, event: Event) {
            val textName: TextView = view.findViewById(R.id.textName)
            val textDescription: TextView = view.findViewById(R.id.textDescription)

            if (event.timeInMinutes > 40) {
                val str = SpannableStringBuilder(event.name)
                str.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, event.name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                textName.text = str
                textDescription.text = event.description
            } else {
                val str = SpannableStringBuilder(view.resources.getString(R.string.agenda_view_event_text, event.name, event.description))
                str.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, event.name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                textName.text = str
            }

            view.setBackgroundColor(event.color)
            textName.setTextColor(event.textColor)
            textDescription.setTextColor(event.textColor)
        }
    }
}