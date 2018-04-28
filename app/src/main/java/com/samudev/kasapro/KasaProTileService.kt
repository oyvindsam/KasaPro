package com.samudev.kasapro

import android.content.Intent
import android.os.IBinder
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import com.samudev.kasapro.control.ControlPresenter
import com.samudev.kasapro.model.Device
import com.samudev.kasapro.util.PreferencesUtil


class KasaProTileService : TileService(), AsyncTaskCaller {

    override fun onClick() {
        super.onClick()
        // Load device here since it can change in the app/elsewhere.
        val device = PreferencesUtil.getKasaDevice(this) ?:
            return Toast.makeText(this, "No device found, can't set light", Toast.LENGTH_SHORT).show()
        if (device.lightOn) {
            // Turn light off completely
            device.lightOn = false
        } else {
            device.lightOn = true
            device.brightness = 100
        }
        PreferencesUtil.saveKasaDevice(this, device)
        ControlPresenter.AdjustLightStateAsync().execute(this, device)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        // TODO: check device state and set icon accordingly
    }

    override fun onStartListening() {
        super.onStartListening()
        // TODO: change icon accordingly to device state (on/off). Do elsewhere since resource heavy?
        Log.v(KasaProTileService::class.java.simpleName, "OnStartListening")
    }

    override fun asyncFinished(any: Any?) {
        val device = any as Device
        Log.v(KasaProTileService::class.java.simpleName, "Light updated: $device")
    }
}