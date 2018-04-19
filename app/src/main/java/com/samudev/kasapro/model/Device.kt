package com.samudev.kasapro.model


class Device(val id: String, val token: String, var lightOn: Boolean, brightnessLevel: Int) {

    var brightness = 0
        set(value) {
            if (value > 100) {
                field = 100
            } else if (value < 0) {
                field = 0
            } else {
                field = value
            }
        }

    init {
        brightness = brightnessLevel
    }

}