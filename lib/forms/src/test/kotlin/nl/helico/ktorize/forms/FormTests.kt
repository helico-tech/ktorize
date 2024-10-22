package nl.helico.ktorize.forms

import nl.helico.ktorize.schemas.default
import nl.helico.ktorize.schemas.notNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class FormTests {

    @Test
    fun basicTest() {

        val form = object : Form() {
            var name by field<String>().notNull()
            var age by field<Int>()
            var agrees by field<Boolean>().default(true)
        }

        val results = form.validate()

        assertEquals(1, results.size)

        assertFails { form.name }
        assertEquals(null, form.age)

        form.name = "John"
        form.age = 42

        assertEquals("John", form.name)
        assertEquals(42, form.age)

        assertEquals(true, form.agrees)
    }
}