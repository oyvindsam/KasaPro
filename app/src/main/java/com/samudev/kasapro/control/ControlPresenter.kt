package com.samudev.kasapro.control

import android.content.Context
import android.os.AsyncTask
import com.samudev.kasapro.model.Device
import com.samudev.kasapro.util.PreferencesUtil
import com.samudev.kasapro.util.Util
import com.samudev.kasapro.web.WebUtil


class ControlPresenter(val controlView: ControlContract.View) : ControlContract.Presenter {

    private var device: Device?

    init {
        controlView.presenter = this

        // TODO: when first connecting to a new device/getting saved one state might be wrong.
        // Possible hack: pass adjustLight with negative brightness parameter and lightOn = true
        device = PreferencesUtil.getKasaDevice(controlView.getContext())
        controlView.updateDeviceDetails(device)  // device can be null -> no device in pref.
    }

    override fun start() {
    }

    override fun adjustLight(brightness: Int, lightOn: Boolean) {
        if (device == null) return controlView.updateDeviceDetails(null)
        device!!.brightness = brightness
        device!!.lightOn = lightOn

        controlView.setLoadingIndicator(true)
        AdjustLightStateAsync().execute(this, device)
    }

    override fun getNewDevice(email: String?, password: String?) {
        if (email == null || password == null || email.isEmpty()) return
        AddNewDeviceAsync().execute(this, email, password)
        controlView.setLoadingIndicator(true)
    }

    override fun updateDevice() {
        if (device == null) return controlView.updateDeviceDetails(null)
        AdjustLightStateAsync().execute(this, device)
    }

    private fun saveDevice(context: Context, device: Device) {
        PreferencesUtil.saveKasaDevice(context, device)
    }

    private fun deviceUpdated(device: Device?) {
        controlView.updateDeviceDetails(device)
        if (device != null) saveDevice(controlView.getContext(), device)
    }

    private class AddNewDeviceAsync: AsyncTask<Any, Void, Device?>() {

        private lateinit var presenter: ControlPresenter

        override fun doInBackground(vararg params: Any?): Device? {
            presenter = params[0] as ControlPresenter
            val email = params[1] as String
            val password = params[2] as String
            val device : Device
            try {
                val token = WebUtil.getToken(Util.getNewUuid(), email, password) ?: return null
                val deviceInfo = WebUtil.getDeviceId(token) ?: return null
                val deviceId = deviceInfo.get(0)
                val deviceName = deviceInfo.get(1)
                device = Device(deviceId, token, deviceName, false, 0)
            } catch (ignore: Exception) {
                return null
            }
            return device
        }

        override fun onPostExecute(device: Device?) {
            super.onPostExecute(device)
            presenter.deviceUpdated(device)
        }
    }

    private class AdjustLightStateAsync: AsyncTask<Any, Void, Device?>() {

        private lateinit var presenter: ControlPresenter

        override fun doInBackground(vararg params: Any?): Device? {
            presenter = params[0] as ControlPresenter
            val device = params[1] as Device

            return WebUtil.adjustLight(device)
        }

        override fun onPostExecute(result: Device?) {
            super.onPostExecute(result)
            presenter.deviceUpdated(result)
        }
    }
}