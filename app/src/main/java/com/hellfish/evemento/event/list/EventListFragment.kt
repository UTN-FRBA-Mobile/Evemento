package com.hellfish.evemento.event.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.EventListAdapter
import com.hellfish.evemento.event.EventFragment
import android.support.design.widget.FloatingActionButton


class EventListFragment : NavigatorFragment() {

    lateinit var eventsRecyclerView: RecyclerView
    var events: ArrayList<Event> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_event_list, container, false)

        eventsRecyclerView = view.findViewById(R.id.events_recycler_view)

        val manager = LinearLayoutManager(context)
        eventsRecyclerView.setLayoutManager(manager)

        val newEvents: ArrayList<Event>? = arguments?.getParcelableArrayList<Event>("events")
        if (newEvents != null) {
            events = newEvents
        }

        eventsRecyclerView.adapter = EventListAdapter(this, events)


        val fab = view.findViewById<FloatingActionButton>(R.id.event_list_fab)
        fab.setOnClickListener {
            // TODO: Validar como inicializar bien el EventFragment en modo edicion de un evento nuevo.
            listener.replaceFragment(EventFragment())
        }
        return view

    }

    fun onSelectedEvent(event:Event) {
        val eventDetailFragment = EventFragment()
        val args = Bundle()
        // TODO: Validar si hace falta algo mas para inicializar bien el EventFragment
        args.putParcelable("event", event)
        eventDetailFragment.arguments = args
        listener.replaceFragment(eventDetailFragment)
    }
}