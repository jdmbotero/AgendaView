package com.github.jdmbotero.agendaview.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.model.Day
import com.github.jdmbotero.agendaview.util.DateManager

class AgendaPagerViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    private var textCurrentDate: TextView = view.findViewById(R.id.textCurrentDate)
    private var contentHours: LinearLayout = view.findViewById(R.id.contentHours)
    private var listEvents: RecyclerView = view.findViewById(R.id.listEvents)

    fun bind(day: Day) {
        textCurrentDate.text = DateManager.getFormatDate(day.date)

        initHours()
        initRecyclerView()
    }

    private fun initHours() {
        for (i in 0..23) {
            contentHours.addView(getTextViewHour(String.format("%02d", i) + ":00"))
        }

        contentHours.addView(getTextViewHour("00:00"))
    }

    private fun getTextViewHour(text: String): TextView {
        val textView = TextView(view.context)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                view.context.resources.getDimension(R.dimen.agenda_view_hour_height).toInt())
        textView.layoutParams = params

        textView.text = text
        textView.gravity = Gravity.END
        return textView
    }

    private fun initRecyclerView() {
        listEvents.isNestedScrollingEnabled = true
    }
}