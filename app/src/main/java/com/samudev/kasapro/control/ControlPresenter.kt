package com.samudev.kasapro.control

import android.os.AsyncTask
import android.util.Log
import com.samudev.kasapro.util.Util
import com.samudev.kasapro.util.WebUtil
import java.util.*


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

    override fun saveToken(token: String?) {
        this.token = token ?: ""
        Log.v(ControlPresenter::class.java.simpleName, "TOKEN: $token")
        getDeviceId(token)
        // TODO: actually save it
    }

    override fun saveDeviceId(deviceId: String?) {
        this.deviceId = deviceId ?: ""
        Log.v(ControlPresenter::class.java.simpleName, "DEVICE ID: $deviceId")
        adjustLight(true, 50, token, deviceId)
        // TODO: actually save it
    }

    private fun saveBrightnessLevel(brightnessLevel: Int) {
        this.brightness = brightnessLevel
        if (brightnessLevel >= 0) controlView.showBrightnessLevel(true, brightnessLevel)
        Log.v(ControlPresenter::class.java.simpleName, "BRIGHTNESS LEVEL: $brightnessLevel")
    }

    override fun getToken(email: String?, password: String?): Boolean {
        if (email == null || password == null) return false
        AskForTokenAsync().execute(this, email, password)
        //TODO: show loading on ui
        return true
    }

    override fun getDeviceId(token: String?): Boolean {
        if (token == null) return false
        AskForDeviceIdAsync().execute(this, token)
        //TODO: show loading on ui
        return true
    }

    override fun adjustLight(lightOn: Boolean?, brightnessLevel: Int?, token: String?, deviceId: String?): Boolean {
        if (lightOn == null || brightnessLevel == null || token == null || deviceId == null) return false
        AdjustLightStateAsync().execute(this, lightOn, brightnessLevel, token, deviceId)
        //TODO: show loading on ui
        return true
    }



    private fun onAsyncComplete() {
        //TODO: controlView.setLoadingIndicator(false)
    }


    class AskForTokenAsync: AsyncTask<Any, Void, String>() {

        private lateinit var presenter: ControlPresenter

        override fun doInBackground(vararg params: Any?): String {
            presenter = params[0] as ControlPresenter
            val email = params[1] as String
            val password = params[2] as String
            return WebUtil.getToken(Util.getNewUuid(), email, password)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            presenter.saveToken(result)
        }
    }

    class AskForDeviceIdAsync: AsyncTask<Any, Void, String>() {

        private lateinit var presenter: ControlPresenter

        override fun doInBackground(vararg params: Any?): String {
            presenter = params[0] as ControlPresenter
            val token = params[1] as String
            return WebUtil.getDeviceId(token)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            presenter.saveDeviceId(result)
        }
    }

    class AdjustLightStateAsync: AsyncTask<Any, Void, Int?>() {

        private lateinit var presenter: ControlPresenter

        override fun doInBackground(vararg params: Any?): Int? {
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