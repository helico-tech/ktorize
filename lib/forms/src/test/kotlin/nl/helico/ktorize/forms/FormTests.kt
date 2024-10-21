package nl.helico.ktorize.forms

import nl.helico.ktorize.schemas.Schema
import nl.helico.ktorize.schemas.deserialize
import nl.helico.ktorize.schemas.notNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class FormTests {

    @Test
    fun basicTest() {
        val form = object : Form() {
            var name by field(Schema.ofType<String>().notNull())
            var age by field(Schema.ofType<String>().deserialize<Int>())
        }

        assertFails { form.name }
        assertEquals(null, form.age)

        form.name = "John"
        form.age = 42

        assertEquals("John", form.name)
        assertEquals(42, form.age)
    }
}