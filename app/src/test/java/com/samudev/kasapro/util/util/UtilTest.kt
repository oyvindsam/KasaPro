package com.samudev.kasapro.util.util

import com.samudev.kasapro.util.Util
import junit.framework.Assert.assertEquals
import org.junit.Test


class UtilTest {

    @Test
    fun testGenerateUuid() {
        assertEquals(Util.getNewUuid().toString().length, 36)
    }
}