package com.samudev.kasapro.web


class LightState(var lightOn: Boolean, brightnessLevel: Int) {

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
