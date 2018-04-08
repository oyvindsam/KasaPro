package com.samudev.kasapro.control

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.samudev.kasapro.R
import kotlinx.android.synthetic.main.activity_control.*


/* 'Control' is the main part of the app (ui), where you would connect to a new device
    and control brightness and on/off state.
*/
class ControlActivity : AppCompatActivity(), ControlContract.View {

    override lateinit var presenter : ControlContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        setSupportActionBar(toolbar)

        fab_add_device.setOnClickListener { view ->
            Snackbar.make(view, resources.getString(R.string.not_implemented), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        //Create the presenter
        presenter = ControlPresenter(this)
        //presenter.getToken("mail", "password")

    }

    override fun showNotImplementedError() {
        return
    }

    override fun setLoadingIndicator(active: Boolean) {
        return
    }

    override fun showBrightnessLevel(on: Boolean, brightnessLevel: Int) {
        return
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_control, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
