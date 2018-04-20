package com.samudev.kasapro.control

import android.content.Context
import com.samudev.kasapro.BasePresenter
import com.samudev.kasapro.BaseView
import com.samudev.kasapro.model.Device


interface ControlContract {

    interface View : BaseView<Presenter> {

        fun showToast(toastMessage: String)
        fun setLoadingIndicator(active: Boolean)
        fun showBrightnessLevel(on: Boolean, brightnessLevel: Int)
        fun deviceUpdated(device: Device)
    }

    interface Presenter : BasePresenter {

        // TODO: use dependency injection (Dagger) to decouple this context parameter
        // https://stackoverflow.com/a/34664466 Presenter should not be aware of the context!
        fun getDevice(context: Context): Device? // get saved device or null
        fun saveDevice(context: Context, device: Device)
        fun getNewDevice(email: String?, password: String?): Boolean  // called after user presses 'log in'
        fun adjustLight(device: Device)
    }


}