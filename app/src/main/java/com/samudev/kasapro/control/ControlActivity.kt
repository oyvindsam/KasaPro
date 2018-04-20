package com.samudev.kasapro.control

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
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
    lateinit var device: Device

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        setSupportActionBar(toolbar)

        // TODO: kind of ugly, should view be aware of device state? Most likely not
        ib_50.setOnClickListener {
            device.brightness = 50
            device.lightOn = true
            presenter.adjustLight(device)
        }
        ib_100.setOnClickListener {
            device.brightness = 100
            device.lightOn = true
            presenter.adjustLight(device)
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
                device.lightOn = seekBar.progress > 0
                device.brightness = seekBar.progress
                presenter.adjustLight(device)
            }
        })

        fab_add_device.setOnClickListener { view ->
            openAddDeviceDialog()
        }

        //Create the presenter
        presenter = ControlPresenter(this)

        device = presenter.getDevice(this) ?: Device("", "", "", false, 0)

        if (device.id.isEmpty()) tv_device_name.text = "No Device"
        else tv_device_name.text = device.name
    }

    fun openAddDeviceDialog() {
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

    override fun deviceUpdated(device: Device) {
        presenter.saveDevice(this, device)
        tv_device_name.text = device.name
        tv_device_brightness.text = device.brightness.toString()
        sb_brightness.progress = device.brightness
        showToast("Device updated: ${device.id}, ${device.brightness}")
    }

    fun addNewDevice(email: String, password: String) {

    }

    override fun showToast(toastMessage: String) {
        Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_SHORT).show()
    }

    override fun setLoadingIndicator(active: Boolean) {
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
