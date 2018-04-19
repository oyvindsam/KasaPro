package com.samudev.kasapro.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceTest {

    @Test
    fun testInitBrightness() {
        val device1 = Device("", "", true, 110)
        assertEquals(100, device1.brightness)

        val device2 = Device("", "", true, 110)
        assertEquals(100, device2.brightness)
    }

    @Test
    fun testSetBrightness() {
        val device = Device("", "", false, 50)
        device.brightness = 111
        assertEquals(100, device.brightness)
    }
}