package com.samudev.kasapro.control

import com.samudev.kasapro.BasePresenter
import com.samudev.kasapro.BaseView


interface ControlContract {

    interface View : BaseView<Presenter> {

        fun showNotImplementedError(toastMessage: String)
        fun setLoadingIndicator(active: Boolean)
        fun showBrightnessLevel(on: Boolean, brightnessLevel: Int)
    }

    interface Presenter : BasePresenter {

        // Change to setupDevice() ?
        fun getNewDevice(email: String?, password: String?): Boolean  // called after user presses 'log in'
        fun adjustLight(lightOn: Boolean?, brightnessLevel: Int?, token: String?, deviceId: String?): Int
    }


}