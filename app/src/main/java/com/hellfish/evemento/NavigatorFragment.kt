package com.hellfish.evemento

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment

open class NavigatorFragment : Fragment() {

    protected lateinit var listener: Navigator

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Navigator) listener = context else throw ClassCastException(context.toString() + " must implement Navigator.")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
    }

    protected open fun setupToolbar() {
        listener.setCustomToolbar(null, true)
    }

}