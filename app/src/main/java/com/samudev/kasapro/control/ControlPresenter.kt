package com.samudev.kasapro.control

import android.os.AsyncTask
import android.util.Log
import com.samudev.kasapro.util.Util
import com.samudev.kasapro.web.WebUtil
import java.util.*
import kotlin.collections.HashMap


class ControlPresenter(val controlView: ControlContract.View) : ControlContract.Presenter {

    private lateinit var token: String
    private lateinit var deviceId: String
    private var brightness = -1

    init {
        controlView.presenter = this
    }

    override fun start() {
        // TODO: load token/device_id from savedPreferences
    }

    override fun getNewDevice(email: String?, password: String?): Boolean {
        if (email == null || password == null || email.isEmpty()) return false
        AddNewDeviceAsync().execute(this, email, password)
        //TODO: show loading on ui
        return true
    }

    fun saveNewDevice(deviceDetails: Map<String, String?>) {
        this.token = deviceDetails.getValue("token") ?: return controlView.showNotImplementedError("Could not load device data..")
        this.deviceId = deviceDetails.getValue("deviceId") ?: return controlView.showNotImplementedError("Could not load device data..")
        Log.v(ControlPresenter::class.java.simpleName, "TOKEN: $token, DEVICEID: $deviceId")
        controlView.showNotImplementedError("User info loaded: $token, $deviceId")
        // TODO: actually save it
    }

    private fun saveBrightnessLevel(brightnessLevel: Int) {
        this.brightness = brightnessLevel
        if (brightnessLevel >= 0) controlView.showBrightnessLevel(true, brightnessLevel)
        Log.v(ControlPresenter::class.java.simpleName, "BRIGHTNESS LEVEL: $brightnessLevel")
    }

    override fun adjustLight(lightOn: Boolean?, brightnessLevel: Int?, token: String?, deviceId: String?): Int {
        if (lightOn == null || brightnessLevel == null || token == null || deviceId == null) return -1
        AdjustLightStateAsync().execute(this, lightOn, brightnessLevel, token, deviceId)
        //TODO: show loading on ui
        return brightnessLevel
    }


    private fun onAsyncComplete() {
        //TODO: controlView.setLoadingIndicator(false)
    }

    class AddNewDeviceAsync: AsyncTask<Any, Void, Map<String, String?>?>() {

        private lateinit var presenter: ControlPresenter

        override fun doInBackground(vararg params: Any?): Map<String, String?>? {
            presenter = params[0] as ControlPresenter
            val email = params[1] as String
            val password = params[2] as String

            val resultMap = HashMap<String, String?>()
            try {
                val token = WebUtil.getToken(Util.getNewUuid(), email, password) ?: return null
                val deviceId = WebUtil.getDeviceId(token) ?: return null
                resultMap.put("token", token)
                resultMap.put("deviceId", deviceId)
            } catch (ignore: Exception) {
                return null
            }
            return resultMap
        }

        override fun onPostExecute(result: Map<String, String?>?) {
            super.onPostExecute(result)
            if (result == null || result.isEmpty()) return //TODO: show error on ui
            presenter.saveNewDevice(result)
        }
    }

    class AdjustLightStateAsync: AsyncTask<Any, Void, Int?>() {

        private lateinit var presenter: ControlPresenter

        override fun doInBackground(vararg params: Any?): Int {
            presenter = params[0] as ControlPresenter
            val lightOn = params[1] as Boolean
            val brightnessLevel = params[2] as Int
            val token = params[3] as String
            val deviceId = params[4] as String
            return WebUtil.adjustLight(lightOn, brightnessLevel, token, deviceId)
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            presenter.saveBrightnessLevel(result ?: -1)
        }
    }
}