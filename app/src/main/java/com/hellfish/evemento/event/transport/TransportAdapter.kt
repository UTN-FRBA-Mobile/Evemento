package com.hellfish.evemento.event.transport;

import android.content.Context
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.CardView
import com.hellfish.evemento.Navigator
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import com.hellfish.evemento.api.User
import com.hellfish.evemento.event.guest.UserColor
import kotlinx.android.synthetic.main.transport_item.view.*

class TransportAdapter(val transportList: List<TransportItem>, private val navigatorListener: Navigator): RecyclerAdapter<CardView, TransportItem>(transportList), UserColor{

    override fun layout(item: Int): Int = R.layout.transport_item

    override fun doOnItemOnBindViewHolder(view: CardView, transport: TransportItem, context: Context) {
        view.txtDriverName.text = transport.driverName()
        view.txtAvailableSlots.text = transport.availableSlots().toString()
        drawDriverCircle(view, transport.driver, context)

        val transportDetailFragment = TransportDetailFragment()
        val args = Bundle()
        args.putParcelable("driver", transport.driver)
        transportDetailFragment.arguments = args
        view.setOnClickListener { navigatorListener.replaceFragment(transportDetailFragment)}
    }

    fun drawDriverCircle(view: CardView, item: User, context: Context) {
        DrawableCompat.setTint(view.driverCircle.drawable, userColor(item.userId, item.displayName))
        view.driverInitial.text = item.displayName.first().toUpperCase().toString()
    }

}
