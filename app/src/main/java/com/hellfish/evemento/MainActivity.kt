package com.hellfish.evemento

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.list.EventListFragment
import android.util.Log
import android.view.MenuItem
import com.hellfish.evemento.event.EventTime
import com.hellfish.evemento.extensions.showSnackbar
import com.hellfish.evemento.extensions.toVisibility

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.nav_header.view.*
import net.danlew.android.joda.JodaTimeAndroid

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer)

        // Make sure we are logged in
        if (!SessionManager.isLoggedIn) {
            showLoginActivity()
        }

        updateNavBarHeader()

        JodaTimeAndroid.init(this)

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            handleNavItemSelected(menuItem)
            Log.d("Selected MenuItem", menuItem.toString())
            true
        }

        if (savedInstanceState == null) {
            val fragment = EventListFragment()
            val args = Bundle()

            // TODO: Cargar de algun lado sin hardcodear...
            val events = arrayListOf(
                    Event("Mock Title 1",
                            "Mock Description 1",
                            EventTime("03/06/2018 - 04:20", "03/06/2018 - 04:20"),
                            "Mock Location 1",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 2",
                            "Mock Description 2",
                            EventTime("03/06/2018 - 04:20", "03/06/2018 - 04:20"),
                            "Mock Location 2",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 3",
                            "Mock Description 3",
                            EventTime("03/06/2018 - 04:20", "03/06/2018 - 04:20"),
                            "Mock Location 3",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 4",
                            "Mock Description 4",
                            EventTime("03/06/2018 - 04:20", "03/06/2018 - 04:20"),
                            "Mock Location 4",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 5",
                            "Mock Description 5",
                            EventTime("03/06/2018 - 04:20", "03/06/2018 - 04:20"),
                            "Mock Location 5",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 6",
                            "Mock Description 6",
                            EventTime("03/06/2018 - 04:20", "03/06/2018 - 04:20"),
                            "Mock Location 6",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")))

            args.putParcelableArrayList("events", events)
            fragment.arguments = args
            supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        }
    }

    private fun updateNavBarHeader() {
        val headerView = navView.getHeaderView(0)
        headerView.navBarUserName.text = SessionManager.currentUser?.displayName
        headerView.navBarUserEmail.text = SessionManager.currentUser?.email
    }

    private fun handleNavItemSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.nav_logout) {
            SessionManager.logout(this) { success, message ->
                if (success) {
                    showSnackbar(message, main_container)
                    showLoginActivity()
                    finish()
                }
            }
        }
    }

    private fun showLoginActivity() {
        val intent = Intent(applicationContext, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun setCustomToolbar(customToolbar: Toolbar?, title: String?) {
        defaultToolbar.visibility= (customToolbar == null).toVisibility()
        setSupportActionBar(customToolbar ?: defaultToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
        supportActionBar?.title = title
    }

    override fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.
                beginTransaction().
                replace(R.id.main_container, fragment).
                addToBackStack(null).
                commit()
    }

}
