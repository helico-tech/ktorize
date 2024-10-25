package nl.helico.ktorize.forms

import nl.helico.ktorize.schemas.Schema

interface FieldType<T> {
    val readSchema: Schema<T>
    val writeSchema: Schema<String?>

    fun read(value: String?): T = readSchema.parse(value)
    fun write(value: T): String? = writeSchema.parse(value)

    companion object {
        operator fun <T> invoke(readSchema: Schema<T>, writeSchema: Schema<String?>): FieldType<T> = object : FieldType<T> {
            override val readSchema: Schema<T> = readSchema
            override val writeSchema: Schema<String?> = writeSchema
        }
    }
}