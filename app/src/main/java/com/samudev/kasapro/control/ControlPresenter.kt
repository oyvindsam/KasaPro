package com.samudev.kasapro.control

import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.AsyncTask
import android.util.Log
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

        // TODO: when first connecting to a new device/getting saved one state might be wrong (changed in another app etc.)
        // Possible hack: pass adjustLight with negative brightness parameter and lightOn = true
        device = PreferencesUtil.getKasaDevice(controlView.getContext())
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

    // TODO: only check current light state, do not change it..
    // Get light info from server. presenter's device can be outdated
    override fun refreshDeviceState() {
        AdjustLightStateAsync().execute(this, device)
    }

    override fun saveDeviceToDisk(context: Context, device: Device?) {
        if (device != null) PreferencesUtil.saveKasaDevice(context, device)
        else PreferencesUtil.saveKasaDevice(context, this.device)
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
            val device = params[1] as Device
            WebUtil.adjustLight(device)
            return WebUtil.adjustLight(device)
        }

        override fun onPostExecute(result: Device?) {
            super.onPostExecute(result)
            asyncTaskCaller.asyncFinished(result)
        }
    }
}