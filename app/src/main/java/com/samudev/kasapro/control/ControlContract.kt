package com.samudev.kasapro.control

import com.samudev.kasapro.BasePresenter
import com.samudev.kasapro.BaseView


interface ControlContract {

    interface View : BaseView<Presenter> {

        fun showNotImplementedError()
        fun setLoadingIndicator(active: Boolean)
        fun showBrightnessLevel(on: Boolean, brightnessLevel: Int)
    }

    interface Presenter : BasePresenter {

        fun getToken(email: String?, password: String?): Boolean
        fun getDeviceId(token: String?): Boolean
        fun adjustLight(lightOn: Boolean?, brightnessLevel: Int?, token: String?, deviceId: String?): Boolean
        fun saveToken(token: String?)
        fun saveDeviceId(deviceId: String?)
    }

}