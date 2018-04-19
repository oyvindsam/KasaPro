package com.samudev.kasapro.control

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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

        fab_add_device.setOnClickListener { view ->
            openAddDeviceDialog()
        }

        //Create the presenter
        presenter = ControlPresenter(this)

        device = presenter.getDevice(this) ?: Device("", "", false, 0)

        if (device.id.isEmpty()) showToast("No device found, click fab to add new")
        else showToast("Device ${device.id} loaded")
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
