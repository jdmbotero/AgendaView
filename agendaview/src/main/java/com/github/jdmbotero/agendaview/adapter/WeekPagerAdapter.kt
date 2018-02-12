package com.github.jdmbotero.agendaview.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.adapter.viewholder.WeekPagerViewHolder
import com.github.jdmbotero.agendaview.model.Day
import io.reactivex.subjects.PublishSubject

class WeekPagerAdapter(items: ArrayList<Day>) : RecyclerView.Adapter<WeekPagerViewHolder>() {

    val observable = PublishSubject.create<Day>()!!

    var items: ArrayList<Day> = items
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_week_day_page, parent, false)
        return WeekPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekPagerViewHolder, position: Int) {
        holder.bind(items[position], observable)
    }
}