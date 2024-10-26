package nl.helico.ktorize.schemas

import kotlin.test.Test
import kotlin.test.assertFails

class SchemaTests {

    val stringSchema = Schema.ofType<String>()
    val intSchema = Schema.ofType<Int>()

    @Test
    fun ofType() {
        val schema = Schema.ofType<String>()

        assert(schema.parse("Hello") == "Hello")
        assert(schema.parse(null) == null)
        assertFails { schema.parse(42) }
    }

    @Test
    fun map() {
        val schema = Schema.ofType<String>().map { it?.toInt() ?: 0 }

        assert(schema.parse("42") == 42)
        assert(schema.parse(null) == 0)
        assertFails { schema.parse(42) }
    }

    @Test
    fun either() {
        val schema = Schema.either(
            stringSchema.map { it?.toInt() },
            intSchema
        )

        assert(schema.parse("42") == 42)
        assert(schema.parse(42) == 42)
    }

    @Test
    fun notNull() {
        val schema = stringSchema.notNull()

        assert(schema.parse("Hello") == "Hello")
        assertFails { schema.parse(null) }
    }

    @Test
    fun deserialize() {
        val schema = stringSchema.deserialize<Int>()

        assert(schema.parse("42") == 42)
        assert(schema.parse(null) == null)
    }
}