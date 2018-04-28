package com.samudev.kasapro.control

import android.content.Context
import com.samudev.kasapro.BasePresenter
import com.samudev.kasapro.BaseView
import com.samudev.kasapro.model.Device


interface ControlContract {

    interface View : BaseView<Presenter> {

        fun showToast(toastMessage: String)
        fun setLoadingIndicator(active: Boolean)
        fun updateDeviceDetails(device: Device?)
        fun getContext(): Context  // TODO: fix this hack with dagger
    }

    interface Presenter : BasePresenter {

        // TODO: use dependency injection (Dagger) to decouple this context parameter
        // https://stackoverflow.com/a/34664466 Presenter should not be aware of the context!
        fun getNewDevice(email: String?, password: String?)
        fun updateDevice()
        fun saveDevice(context: Context, device: Device?)
        fun loadDevice(context: Context) : Device?
        fun adjustLight(brightness: Int, lightOn: Boolean)
    }


}