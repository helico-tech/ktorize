package nl.helico.ktorize.forms

import kotlin.test.Test
import kotlin.test.assertEquals

class FormTests {

    @Test
    fun basicTest() {
        val form = object : Form() {
            var name by scalar<String>().default { "John" }
            var age by scalar<Int>()
            val agreed by scalar<Boolean>()
        }

//        assertEquals("John", form.name)
//        form.name = "Jane"
//        assertEquals("Jane", form.name)
//
//        assertEquals(null, form.age)
//        form.age = 42
//        assertEquals(42, form.age)
    }
}