package ch.thipok.method

import kotlin.test.Test
import kotlin.test.assertTrue

class JvmGreetingTest {

    @Test
    fun testExample() {
        println(Greeting().sentence())
        assertTrue(Greeting().sentence().contains("J"), "Check J is mentioned")
    }
}