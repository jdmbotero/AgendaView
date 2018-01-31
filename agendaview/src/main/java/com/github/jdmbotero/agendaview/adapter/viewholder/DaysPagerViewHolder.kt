package com.github.jdmbotero.agendaview.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.model.Day
import com.github.jdmbotero.agendaview.model.Week
import com.github.jdmbotero.agendaview.util.DateManager
import com.github.jdmbotero.agendaview.util.Utils
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.subjects.PublishSubject

class DaysPagerViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    fun bind(week: Week, observable: PublishSubject<Day>) {
        (view.rootView as LinearLayout).removeAllViews()
        week.days.forEach { day ->
            val viewDay = getDay(day)
            RxView.clicks(viewDay).subscribe {
                observable.onNext(day)
            }
            (view.rootView as LinearLayout).addView(viewDay)
        }
    }

    private fun getDay(day: Day): LinearLayout {
        val linearLayout = LinearLayout(view.context)

        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.weight = 1f

        linearLayout.layoutParams = params
        linearLayout.gravity = Gravity.CENTER
        linearLayout.orientation = LinearLayout.VERTICAL

        linearLayout.addView(getTextView(day, true))
        linearLayout.addView(getTextView(day, false))

        return linearLayout
    }

    private fun getTextView(day: Day, isName: Boolean): TextView {
        val textView = TextView(view.context)

        val screenSize = Utils.getScreenSize(view.context)

        val params = LinearLayout.LayoutParams(screenSize[0] / 10, screenSize[0] / 10)
        params.setMargins(0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5f, view.resources.displayMetrics).toInt(), 0, 0)
        textView.layoutParams = params

        textView.text = if (isName) DateManager.getFormatDate(day.date, "EEE")
        else DateManager.getFormatDate(day.date, "dd")

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                if (isName) view.resources.getDimension(R.dimen.agenda_view_day_name_size)
                else view.resources.getDimension(R.dimen.agenda_view_day_size))

        textView.gravity = Gravity.CENTER

        if (day.isToday) textView.setTextColor(view.resources.getColor(R.color.colorAccent))
        else textView.setTextColor(view.resources.getColor(R.color.colorTextPrimary))

        if (!isName) {
            if (day.isSelected)
                textView.background = view.resources.getDrawable(R.drawable.agenda_view_bg_day_selected)
            else
                textView.background = view.resources.getDrawable(R.drawable.agenda_view_bg_day)
        }

        return textView
    }
}