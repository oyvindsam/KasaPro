package com.samudev.kasapro

import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import com.samudev.kasapro.control.ControlPresenter
import com.samudev.kasapro.model.Device
import com.samudev.kasapro.util.PreferencesUtil


class KasaProTileService : TileService(), AsyncTaskCaller {

    private val LOG_TAG = KasaProTileService::class.java.simpleName
    private var device : Device? = null

    override fun onClick() {
        super.onClick()
        Log.v(LOG_TAG, "From pref: $device")
        val lightOn = device?.lightOn ?: return Toast.makeText(this, "No device found, can't set light", Toast.LENGTH_SHORT).show()
        if (lightOn) {
            // Turn light off completely
            device?.lightOn = false
        } else {
            device?.lightOn = true
            device?.brightness = 100
        }
        Log.v(LOG_TAG, "Setting state: $device")
        ControlPresenter.AdjustLightStateAsync().execute(this, device)
        PreferencesUtil.saveKasaDevice(this, device)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        // TODO: check device state and set icon accordingly
        Log.v(LOG_TAG, "onTileAdded---")
    }

    override fun onStartListening() {
        super.onStartListening()
        // TODO: change icon accordingly to device state (on/off). Do elsewhere since resource heavy?
        device = PreferencesUtil.getKasaDevice(this) ?: return
        Log.v(KasaProTileService::class.java.simpleName, "onStartListening---")
    }

    override fun asyncFinished(any: Any?) {
        val device = any as Device
        Log.v(KasaProTileService::class.java.simpleName, "Light updated to: $device")
    }
}