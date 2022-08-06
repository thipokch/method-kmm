package ch.thipok.method

import kotlin.test.Test
import kotlin.test.assertTrue

class PlaceholderTest {

    @Test
    fun testExample() {
        assertTrue(Placeholder().sentence().contains("Hello"), "Check 'Hello' is mentioned")
    }
}