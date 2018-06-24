package com.hellfish.evemento

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.hellfish.evemento.event.list.EventListFragment
import android.util.Log
import android.view.MenuItem
import com.hellfish.evemento.extensions.showSnackbar
import com.hellfish.evemento.extensions.toVisibility
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.nav_header.view.*
import net.danlew.android.joda.JodaTimeAndroid

class MainActivity : AppCompatActivity(), Navigator {

    override var onBackPressedListener: OnBackPressedListener? = null

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
            // Cargo el EventListFragment sin pasarle argumento porque la llamada para pedir eventos la hace despues.
            supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        }
    }


    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            onBackPressedListener != null -> onBackPressedListener?.invoke()
            else -> super.onBackPressed()
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
        onBackPressedListener = null
        supportFragmentManager.
                beginTransaction().
                replace(R.id.main_container, fragment).
                addToBackStack(null).
                commit()
    }

}