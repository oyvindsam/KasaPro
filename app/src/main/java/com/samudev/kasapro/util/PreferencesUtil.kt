package com.samudev.kasapro.util

import android.content.Context
import android.preference.PreferenceManager
import com.samudev.kasapro.model.Device


class PreferencesUtil {
    companion object {

        private const val KASA_EMAIL = "com.samudev.kasapro.email"
        private const val KASA_DEVICE_ID = "com.samudev.kasapro.device_id"
        private const val KASA_DEVICE_TOKEN = "com.samudev.kasapro.token"
        private const val KASA_DEVICE_NAME = "com.samudev.kasapro.name"
        private const val KASA_DEVICE_BRIGHTNESS = "com.samudev.kasapro.brightness"
        private const val KASA_DEVICE_LIGHT_STATE = "com.samudev.kasapro.light_state"

        fun getKasaEmail(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(KASA_EMAIL, "")
        }

        fun saveKasaEmail(context: Context, email: String) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(KASA_EMAIL, email)
            editor.apply()
        }

        /**
         * Get device details or return if one or more parameter does not exist
         */
        fun getKasaDevice(context: Context): Device? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            val deviceId = preferences.getString(KASA_DEVICE_ID, null) ?: return null
            val deviceToken = preferences.getString(KASA_DEVICE_TOKEN, null) ?: return null
            val deviceName = preferences.getString(KASA_DEVICE_NAME, null) ?: return null
            val deviceBrightness = preferences.getInt(KASA_DEVICE_BRIGHTNESS, 0)
            val deviceLightState = preferences.getBoolean(KASA_DEVICE_LIGHT_STATE, false)
            if (deviceId.isEmpty() || deviceToken.isEmpty()) return null

            return Device(deviceId, deviceToken, deviceName, deviceLightState, deviceBrightness)
        }

        fun saveKasaDevice(context: Context, device: Device?) {
            if (device == null) return
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(KASA_DEVICE_ID, device.id)
            editor.putString(KASA_DEVICE_TOKEN, device.token)
            editor.putString(KASA_DEVICE_NAME, device.name)
            editor.putInt(KASA_DEVICE_BRIGHTNESS, device.brightness)
            editor.putBoolean(KASA_DEVICE_LIGHT_STATE, device.lightOn)
            editor.apply()
        }
    }
}