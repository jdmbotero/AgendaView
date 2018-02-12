package com.github.jdmbotero.agendaview.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.jdmbotero.agendaview.AgendaView
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.model.Day
import com.github.jdmbotero.agendaview.util.DateManager
import com.github.jdmbotero.agendaview.util.Utils
import io.reactivex.subjects.PublishSubject

class WeekPagerViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    fun bind(day: Day, observable: PublishSubject<Day>) {
        val textDay = (view as LinearLayout).getChildAt(0) as TextView
        val textName = (view as LinearLayout).getChildAt(1) as TextView

        val screenSize = Utils.getScreenSize(view.context)

        val params = LinearLayout.LayoutParams((screenSize[0] / 7) - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, view.resources.displayMetrics).toInt(),
                (screenSize[0] / 7) - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, view.resources.displayMetrics).toInt())
        textDay.layoutParams = params

        textDay.text = DateManager.getFormatDate(day.date, "dd")
        textName.text = DateManager.getFormatDate(day.date, "EEE")

        if (day.isToday) {
            textDay.setTextColor(AgendaView.dayCurrentColor)
            textName.setTextColor(AgendaView.dayCurrentColor)
        } else {
            textDay.setTextColor(AgendaView.dayTextColor)
            textName.setTextColor(AgendaView.dayTextColor)
        }

        if (day.isSelected) textDay.background = AgendaView.daySelectedBackground
        else textDay.background = AgendaView.dayBackground

        view.setOnClickListener {
            observable.onNext(day)
        }
    }
}