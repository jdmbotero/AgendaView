package com.github.jdmbotero.agendaview.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.adapter.viewholder.DayPagerViewHolder
import com.github.jdmbotero.agendaview.model.Day
import io.reactivex.subjects.PublishSubject
import java.util.*

class DayPagerAdapter(items: ArrayList<Day>) : RecyclerView.Adapter<DayPagerViewHolder>() {

    val observable = PublishSubject.create<Calendar>()!!

    var items: ArrayList<Day> = items
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_day_page, parent, false)
        return DayPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayPagerViewHolder, position: Int) {
        holder.bind(items[position])
    }
}