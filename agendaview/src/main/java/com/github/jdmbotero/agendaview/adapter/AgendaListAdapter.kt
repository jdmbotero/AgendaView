package com.github.jdmbotero.agendaview.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.adapter.viewholder.AgendaListViewHolder
import com.github.jdmbotero.agendaview.model.Event

class AgendaListAdapter(items: ArrayList<Event>) : RecyclerView.Adapter<AgendaListViewHolder>() {

    var items: ArrayList<Event> = items
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_agenda_list, parent, false)
        return AgendaListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgendaListViewHolder, position: Int) {
        holder.bind(items[position], if (position > 0) items[position - 1] else null)
    }
}