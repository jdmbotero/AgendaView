package com.github.jdmbotero.agendaview.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.model.Week
import com.github.jdmbotero.agendaview.util.DateManager
import java.util.*

class DaysPagerViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    fun bind(week: Week) {
        (view.rootView as LinearLayout).removeAllViews()
        week.days.forEach { day ->
            (view.rootView as LinearLayout).addView(getDay(day.date))
        }
    }

    private fun getDay(date: Calendar): LinearLayout {
        val linearLayout = LinearLayout(view.context)

        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.weight = 1f

        linearLayout.layoutParams = params
        linearLayout.gravity = Gravity.CENTER
        linearLayout.orientation = LinearLayout.VERTICAL

        linearLayout.addView(getTextView(DateManager.getFormatDate(date, "EEE"),
                view.resources.getDimension(R.dimen.agenda_view_day_name_size)))
        linearLayout.addView(getTextView(DateManager.getFormatDate(date, "dd"),
                view.resources.getDimension(R.dimen.agenda_view_day_size)))

        return linearLayout
    }

    private fun getTextView(text: String, textSize: Float): TextView {
        val textView = TextView(view.context)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)

        textView.layoutParams = params

        textView.text = text
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        textView.gravity = Gravity.CENTER
        return textView
    }
}