package ch.thipok.method

import kotlin.test.Test
import kotlin.test.assertTrue

class JsGreetingTest {

    @Test
    fun testExample() {
        println(Greeting().sentence())
        assertTrue(Greeting().sentence().isNotEmpty(), "Check browser's name is mentioned")
    }
}