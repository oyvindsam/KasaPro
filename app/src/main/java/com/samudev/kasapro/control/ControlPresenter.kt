package com.samudev.kasapro.control

import android.content.Context
import android.os.AsyncTask
import com.samudev.kasapro.AsyncTaskCaller
import com.samudev.kasapro.model.Device
import com.samudev.kasapro.util.PreferencesUtil
import com.samudev.kasapro.util.Util
import com.samudev.kasapro.web.WebUtil


/**
 * Controls all ui elements and updates to those. Activates/deactivates views.
 */
class ControlPresenter(private val controlView: ControlContract.View) : ControlContract.Presenter, AsyncTaskCaller {

    private val LOG_TAG = ControlPresenter::class.java.simpleName

    private var device: Device?

    init {
        controlView.presenter = this

        device = PreferencesUtil.getKasaDevice(controlView.getContext())
        ControlPresenter.AdjustLightStateAsync().execute(this, device, false)
        controlView.updateDeviceDetails(device)  // device can be null -> no device in pref.
    }

    override fun start() {
    }

    override fun adjustLight(brightness: Int, lightOn: Boolean) {
        if (device == null) return controlView.updateDeviceDetails(null)
        device?.brightness = brightness
        device?.lightOn = lightOn

        controlView.setLoadingIndicator(true)
        AdjustLightStateAsync().execute(this, device)
    }

    override fun getNewDevice(email: String?, password: String?) {
        if (email == null || password == null || email.isEmpty()) return
        controlView.setLoadingIndicator(true)
        AddNewDeviceAsync().execute(this, email, password)
    }

    // Get light info from server. presenter's device can be outdated
    override fun refreshDeviceState() {
        AdjustLightStateAsync().execute(this, device, false)
    }

    override fun saveDeviceToDisk(context: Context, device: Device?) {
        if (device != null) PreferencesUtil.saveKasaDevice(context, device)
        else return
    }

    override fun loadDeviceFromDisk(context: Context): Device? {
        device = PreferencesUtil.getKasaDevice(context)
        return device
    }

    override fun asyncFinished(any: Any?) {
        this.device = any as? Device
        controlView.updateDeviceDetails(device)
        saveDeviceToDisk(controlView.getContext(), device)
    }

    private class AddNewDeviceAsync: AsyncTask<Any, Void, Device?>() {

        private lateinit var asyncTaskCaller: AsyncTaskCaller

        override fun doInBackground(vararg params: Any?): Device? {
            asyncTaskCaller = params[0] as AsyncTaskCaller
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
            asyncTaskCaller.asyncFinished(device)
        }
    }

    public class AdjustLightStateAsync: AsyncTask<Any, Void, Device?>() {

        private lateinit var asyncTaskCaller: AsyncTaskCaller

        override fun doInBackground(vararg params: Any?): Device? {
            asyncTaskCaller = params[0] as AsyncTaskCaller
            if (params[1] == null) return null
            val device = params[1] as Device
            var updateDeviceState = true
            if (params.size == 3) updateDeviceState = params[2] as Boolean
            WebUtil.queryDevice(device, updateDeviceState)
            return WebUtil.queryDevice(device, updateDeviceState)
        }

        override fun onPostExecute(result: Device?) {
            super.onPostExecute(result)
            asyncTaskCaller.asyncFinished(result)
        }
    }
}