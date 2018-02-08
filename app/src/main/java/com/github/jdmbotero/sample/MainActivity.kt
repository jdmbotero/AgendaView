package com.github.jdmbotero.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import com.github.jdmbotero.agendaview.model.Event
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val rnd = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addEvents()
        //addEvent()
    }

    private fun addEvent() {
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.HOUR_OF_DAY, 2)
        startDate.set(Calendar.MINUTE, 0)
        startDate.set(Calendar.SECOND, 0)
        startDate.set(Calendar.MILLISECOND, 0)

        val event = Event(
                "Lorem ipsum ",
                "lorem ipsum dolor sit amet",
                startDate,
                60,
                Color.parseColor("#6cbc05"),
                Color.parseColor("#6cbc05"))

        agendaView.events = arrayListOf(event)
    }

    private fun addEvents() {
        val currentDate = Calendar.getInstance()
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)

        val events = ArrayList<Event>()

        for (i in 0..23) {
            val startDate = Calendar.getInstance()
            startDate.time = currentDate.time
            startDate.add(Calendar.HOUR_OF_DAY, i * 2)

            val event = Event(
                    "Lorem ipsum ",
                    "lorem ipsum dolor sit amet",
                    startDate,
                    90,
                    Color.parseColor("#24a9df"),
                    Color.parseColor("#24a9df"))

            if (i == 0) {
                event.textGravity = Gravity.CENTER
            }

            events.add(event)
        }

        agendaView.events = events
    }
}