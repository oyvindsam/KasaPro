package com.samudev.kasapro

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.samudev.kasapro.control.ControlPresenter
import com.samudev.kasapro.model.Device
import com.samudev.kasapro.util.PreferencesUtil


class KasaProTileService : TileService(), AsyncTaskCaller {

    /**
     * Should try to get current device and set light to either 100% or OFF.
     */
    override fun onClick() {
        super.onClick()
        val device = PreferencesUtil.getKasaDevice(this) ?: return Toast.makeText(this, "No device found, can't set light", Toast.LENGTH_SHORT).show()
        device.lightOn = !device.lightOn  // Toggle
        if (device.lightOn) device.brightness = 100  // If light is going to be [on] -> turn brightness to 100

        toggleTile(device.lightOn)  // toggle to new state
        ControlPresenter.AdjustLightStateAsync().execute(this, device)
        PreferencesUtil.saveKasaDevice(this, device)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        val device = PreferencesUtil.getKasaDevice(this) ?: return  // return if user has not added a device
        toggleTile(device.lightOn)
    }

    override fun onStartListening() {
        super.onStartListening()
        val device = PreferencesUtil.getKasaDevice(this) ?: return
        toggleTile(device.lightOn)
    }

    private fun toggleTile(on: Boolean) {
        val tile = qsTile
        // return if state is already correct
        if (tile.state == Tile.STATE_ACTIVE && on ||
                tile.state == Tile.STATE_INACTIVE && !on) return

        if (on) {
            tile.icon = Icon.createWithResource(this, R.drawable.ic_tile_brightness_100)
            tile.label = getString(R.string.tile_kasapro_on)
            tile.state = Tile.STATE_ACTIVE
        } else {
            tile.icon = Icon.createWithResource(this, R.drawable.ic_tile_brightness_0)
            tile.label = getString(R.string.tile_kasapro_off)
            tile.state = Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }

    override fun asyncFinished(any: Any?) {
        if (any is Device) toggleTile(any.lightOn)
        else Toast.makeText(this, "Could not update light", Toast.LENGTH_SHORT).show()
    }
}