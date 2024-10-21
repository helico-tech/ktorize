package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.helico.ktorize.schemas.Schema
import nl.helico.ktorize.schemas.map

abstract class Form(
    initialParameters: Parameters = Parameters.Empty,
    val serializersModule: SerializersModule = Json.serializersModule
) {
    private val parametersBuilder = ParametersBuilder().apply { appendAll(initialParameters) }

    private val delegates = mutableMapOf<String, Field<*>>()

    fun <T> field(type: FieldType<T>): Field<T> {
        val field = Field(type, parametersBuilder) {
            delegates[it.name] = it
        }
        return field
    }

    inline fun <reified T> field(builder: Schema<String?>.() -> Schema<T>): Field<T> {
        val schema = builder(Schema.ofType<String?>())
        return FieldType(schema)
    }

    inline operator fun <reified T> FieldType.Companion.invoke(schema: Schema<T>): Field<T> {
        if (T::class == String::class) {
            val fieldType = invoke(schema, Schema.ofType<String?>())
            return field(fieldType)
        }

        val writeSchema = Schema.ofType<T?>().map {
            it?.let { Json.encodeToString(serializersModule.serializer(), it) }
        }

        val fieldType = invoke(schema, writeSchema)
        return field(fieldType)
    }
}
