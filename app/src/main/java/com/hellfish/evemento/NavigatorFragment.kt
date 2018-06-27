package com.hellfish.evemento

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.hellfish.evemento.R.string.app_name

open class NavigatorFragment : Fragment() {

    protected open val titleId: Int = app_name
    lateinit var navigatorListener: Navigator

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Navigator) navigatorListener = context else throw ClassCastException(context.toString() + " must implement Navigator.")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
    }

    protected open fun setupToolbar() {
        navigatorListener.setCustomToolbar(title = resources.getString(titleId))
    }

    protected fun showToast(stringId: Int) = Toast.makeText(activity, getString(stringId), Toast.LENGTH_LONG).show()

}