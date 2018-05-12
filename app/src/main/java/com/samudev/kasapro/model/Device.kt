package com.samudev.kasapro.model


data class Device(val id: String, val token: String, val name: String, var lightOn: Boolean, var brightness: Int)