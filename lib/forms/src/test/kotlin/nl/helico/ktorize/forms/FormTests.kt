package nl.helico.ktorize.forms

import io.ktor.http.*
import nl.helico.ktorize.schemas.notNull
import kotlin.test.Test
import kotlin.test.assertEquals

class FormTests {

    @Test
    fun fieldString() {
        val parameters = Parameters.build {
            append("name", "John")
        }

        val form = object : Form(parameters) {
            var name by field<String>().notNull()
            var lastName by field<String>()
        }

        assertEquals("John", form.name)
        assertEquals(null, form.lastName)

        form.name = "Alice"
        form.lastName = "Doe"

        assertEquals("Alice", form.name)
        assertEquals("Doe", form.lastName)
    }

    @Test
    fun fieldNonStrings() {
        val parameters = Parameters.build {
            append("age", "25")
            append("height", "1.80")
        }

        val form = object : Form(parameters) {
            var age by field<Int>().notNull()
            var height by field<Double>().notNull()
            var valid by field<Boolean>()
        }

        assertEquals(25, form.age)
        assertEquals(1.80, form.height)
        assertEquals(null, form.valid)

        form.age = 30
        form.height = 1.85
        form.valid = true

        assertEquals(30, form.age)
        assertEquals(1.85, form.height)
        assertEquals(true, form.valid)
    }

    @Test
    fun fieldMultipleStrings() {
        val parameters = Parameters.build {
            append("names", "John")
            append("names", "Jane")
        }

        val form = object : Form(parameters) {
            var names by field<String>().multiple()
        }

        assertEquals(listOf("John", "Jane"), form.names)

        form.names = listOf("Alice", "Bob")

        assertEquals(listOf("Alice", "Bob"), form.names)
    }
}