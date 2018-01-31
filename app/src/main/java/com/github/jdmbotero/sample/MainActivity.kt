package com.github.jdmbotero.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.jdmbotero.agendaview.model.Event
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addEvents()
    }

    private fun addEvents() {
        val rnd = Random()

        val currentDate = Calendar.getInstance()
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)

        val events = ArrayList<Event>()

        for (i in 0..10) {
            val startDate = Calendar.getInstance()
            startDate.time = currentDate.time
            startDate.add(Calendar.HOUR_OF_DAY, i)

            events.add(Event(
                    "Lorem ipsum ",
                    "lorem ipsum dolor sit amet",
                    startDate,
                    rnd.nextInt(30) + 30))
        }

        agendaView.events = events
    }
}