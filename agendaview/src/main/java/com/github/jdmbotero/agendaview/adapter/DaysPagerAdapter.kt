package com.github.jdmbotero.agendaview.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.adapter.viewholder.DaysPagerViewHolder
import com.github.jdmbotero.agendaview.model.Week

class DaysPagerAdapter(items: ArrayList<Week>) : RecyclerView.Adapter<DaysPagerViewHolder>() {

    var items: ArrayList<Week> = items
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_days_page, parent, false)
        return DaysPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaysPagerViewHolder, position: Int) {
        holder.bind(items[position])
    }

}