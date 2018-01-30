package com.github.jdmbotero.agendaview.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.jdmbotero.agendaview.R
import com.github.jdmbotero.agendaview.adapter.viewholder.AgendaPagerViewHolder
import com.github.jdmbotero.agendaview.model.Day

class AgendaPagerAdapter(items: ArrayList<Day>) : RecyclerView.Adapter<AgendaPagerViewHolder>() {

    var items: ArrayList<Day> = items
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_agenda_page, parent, false)
        return AgendaPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgendaPagerViewHolder, position: Int) {
        holder.bind(items[position])
    }

}