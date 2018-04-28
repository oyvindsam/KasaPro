package com.samudev.kasapro.model


class Device(val id: String, val token: String, val name: String, var lightOn: Boolean, brightnessLevel: Int) {

    var brightness = 0
        set(value) {
            if (value > 100) {
                field = 100
            } else {
                field = value
            }
        }

    init {
        brightness = brightnessLevel
    }

    override fun toString(): String {
        return "Device(id='$id', token='$token', name='$name', lightOn=$lightOn, brightness=$brightness)"
    }


}