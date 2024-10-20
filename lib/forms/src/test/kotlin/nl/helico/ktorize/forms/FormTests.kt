package nl.helico.ktorize.forms

import kotlin.test.Test
import kotlin.test.assertEquals

class FormTests {

    @Test
    fun basicTest() {
        val form = object : Form() {
            val name by parameter<String>()
            var age by parameter<Int>()
            var height by parameter<Double>()
            var active by parameter<Boolean>()
        }

        assertEquals("John", form.name)
    }
}