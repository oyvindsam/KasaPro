package com.samudev.kasapro.web

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class LightStateTest {



    @Test
    fun testInitBrightness() {
        val lightState1 = LightState(true, 110)
        assertEquals(100, lightState1.brightness)

        val lightState2 = LightState(true, 110)
        assertEquals(100, lightState2.brightness)
    }

    @Test
    fun testSetBrightness() {
        val lightState = LightState(false, 50)
        lightState.brightness = 111
        assertEquals(100, lightState.brightness)
    }
}