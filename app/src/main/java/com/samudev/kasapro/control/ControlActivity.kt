package com.samudev.kasapro.control

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.samudev.kasapro.R
import com.samudev.kasapro.model.Device
import kotlinx.android.synthetic.main.activity_control.*

/**
 * 'Control' is the main part of the app (ui), where you would connect to a new device
and control brightness and on/off state.
 */
class ControlActivity : AppCompatActivity(), ControlContract.View {

    override lateinit var presenter : ControlContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        setSupportActionBar(toolbar)

        //Create the presenter
        presenter = ControlPresenter(this)

        ib_50.setOnClickListener {
            // TODO: add on/off ability
            presenter.adjustLight(50, true)
        }
        ib_100.setOnClickListener {
            presenter.adjustLight(100, true)
        }

        swipe_refresh.apply {
            setOnRefreshListener { presenter.updateDevice() }
        }

        sb_brightness.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar == null) return
                tv_device_brightness.text = seekBar.progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar == null) return
                presenter.adjustLight(seekBar.progress, seekBar.progress > 0)
            }
        })

        fab_add_device.setOnClickListener { view ->
            openAddDeviceDialog()
        }
    }

    override fun getContext(): Context {
        return applicationContext
    }

    override fun updateDeviceDetails(device: Device?) {
        setLoadingIndicator(false)
        if (device == null) {
            tv_device_name.text = "No device found"
            tv_device_brightness.text = "0"
            sb_brightness.progress = 0
            showToast("Press fab to add new device")
        } else {
            tv_device_name.text = device.name
            tv_device_brightness.text = device.brightness.toString()
            sb_brightness.progress = device.brightness
        }
    }

    private fun openAddDeviceDialog() {
        val builder = AlertDialog.Builder(this);
        val inputView = layoutInflater.inflate(R.layout.signin_dialog, null)
        val emailTextView = inputView.findViewById(R.id.tv_email) as TextView
        val passwordTextView = inputView.findViewById(R.id.tv_password) as TextView
        builder.setView(inputView)
                .setTitle("Log in")
                .setMessage("Enter your Kasa account details")
                .setPositiveButton("Log in", DialogInterface.OnClickListener { dialogInterface, i ->
                    presenter.getNewDevice(emailTextView.text.toString(), passwordTextView.text.toString())
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i -> })
                .show()
    }

    override fun showToast(toastMessage: String) {
        Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_SHORT).show()
    }

    override fun setLoadingIndicator(active: Boolean) {
        with(swipe_refresh) {
            post {isRefreshing = active }
        }
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
