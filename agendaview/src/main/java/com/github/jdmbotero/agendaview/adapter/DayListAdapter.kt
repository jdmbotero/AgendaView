package com.github.jdmbotero.agendaview.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.adapter.viewholder.DayListViewHolder
import com.github.jdmbotero.agendaview.model.Event

class DayListAdapter(items: ArrayList<Event>) : RecyclerView.Adapter<DayListViewHolder>() {

    var items: ArrayList<Event> = items
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_day_list, parent, false)
        return DayListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayListViewHolder, position: Int) {
        holder.bind(items[position], if (position > 0) items[position - 1] else null)
    }
}