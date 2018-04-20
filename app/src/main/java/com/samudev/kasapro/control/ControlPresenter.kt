package com.samudev.kasapro.control

import android.content.Context
import android.os.AsyncTask
import com.samudev.kasapro.model.Device
import com.samudev.kasapro.util.PreferencesUtil
import com.samudev.kasapro.util.Util
import com.samudev.kasapro.web.WebUtil


class ControlPresenter(val controlView: ControlContract.View) : ControlContract.Presenter {

    init {
        controlView.presenter = this
    }

    override fun start() {
    }

    override fun getDevice(context: Context): Device? {
        return PreferencesUtil.getKasaDevice(context)
    }

    override fun saveDevice(context: Context, device: Device) {
        PreferencesUtil.saveKasaDevice(context, device)
    }

    override fun getNewDevice(email: String?, password: String?): Boolean {
        if (email == null || password == null || email.isEmpty()) return false

        AddNewDeviceAsync().execute(this, email, password)
        //TODO: show loading on ui
        return true
    }

    private fun deviceUpdated(device: Device?) {
        if (device == null) return controlView.showToast("Could not load device data..")
        controlView.deviceUpdated(device)
    }

    override fun adjustLight(device: Device) {
        AdjustLightStateAsync().execute(this, device)
        //TODO: show loading on ui
    }

    private fun onAsyncComplete() {
        //TODO: controlView.setLoadingIndicator(false)
    }

    class AddNewDeviceAsync: AsyncTask<Any, Void, Device?>() {

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

    class AdjustLightStateAsync: AsyncTask<Any, Void, Device?>() {

        private lateinit var presenter: ControlPresenter

        override fun doInBackground(vararg params: Any?): Device? {
            presenter = params[0] as ControlPresenter
            val device = params[1] as Device

            return WebUtil.adjustLight(device)
        }

        override fun onPostExecute(result: Device?) {
            super.onPostExecute(result)
            if (result == null) return
            presenter.deviceUpdated(result)
        }
    }
}