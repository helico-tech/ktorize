package nl.helico.ktorize.forms

import kotlin.test.Test
import kotlin.test.assertEquals

class FormTests {

    @Test
    fun basicTest() {
        val form = object : Form() {
            val name by scalar<String>().withDefault("John")
            val age by scalar<Int>()
        }

        assertEquals("John", form.name.get())
        form.name.set("Jane")
        assertEquals("Jane", form.name.get())

        assertEquals(null, form.age.getOrNull())
        form.age.set(42)
        assertEquals(42, form.age.getOrNull())
    }
}