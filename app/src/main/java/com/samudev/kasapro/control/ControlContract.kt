package com.samudev.kasapro.control

import android.content.Context
import com.samudev.kasapro.BasePresenter
import com.samudev.kasapro.BaseView
import com.samudev.kasapro.model.Device


interface ControlContract {

    interface View : BaseView<Presenter> {

        fun showToast(toastMessage: String)  // Display a Toast with some info
        fun setLoadingIndicator(active: Boolean)
        fun updateDeviceDetails(device: Device?)  // update ui with new device info/no device found
        fun getContext(): Context  // TODO: fix this hack with dagger
    }

    interface Presenter : BasePresenter {

        // TODO: use dependency injection (Dagger) to decouple this context parameter
        // https://stackoverflow.com/a/34664466 Presenter should not be aware of the context!
        fun getNewDevice(email: String?, password: String?)
        fun refreshDeviceState()  // Query REST api for device state
        fun saveDeviceToDisk(context: Context, device: Device?)  // Save current device state to disk (pref)
        fun loadDeviceFromDisk(context: Context) : Device?  // Load device from disk (pref)
        fun adjustLight(brightness: Int, lightOn: Boolean)
    }


}