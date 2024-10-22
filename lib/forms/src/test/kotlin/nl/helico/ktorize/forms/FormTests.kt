package nl.helico.ktorize.forms

import io.ktor.http.*
import nl.helico.ktorize.schemas.notNull
import kotlin.test.Test
import kotlin.test.assertEquals

class FormTests {

    @Test
    fun singleString() {
        val parameters = Parameters.build {
            append("name", "John")
        }

        val form = object : Form(parameters) {
            val name by field<String>().notNull()
            val lastName by field<String>()
        }

        assertEquals("John", form.name)
        assertEquals(null, form.lastName)
    }

    @Test
    fun singleNonStrings() {
        val parameters = Parameters.build {
            append("age", "25")
            append("height", "1.80")
        }

        val form = object : Form(parameters) {
            val age by field<Int>().notNull()
            val height by field<Double>().notNull()
            val valid by field<Boolean>()
        }

        assertEquals(25, form.age)
        assertEquals(1.80, form.height)
        assertEquals(null, form.valid)
    }
}