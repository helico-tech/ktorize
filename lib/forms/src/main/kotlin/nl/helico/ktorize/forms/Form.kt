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

    fun <T> field(readSchema: Schema<T>, writeSchema: Schema<String?>): Field<T> {
        val field = SchemaBasedField(readSchema, writeSchema, parametersBuilder) {
            delegates[it.name] = it
        }
        return field
    }

    inline fun <reified T> field(readSchema: Schema<T>): Field<T> {
        // overwrite String class, because it is already properly formatted
        if (T::class == String::class) {
            return field(readSchema, Schema.ofType<String?>())
        }

        val writeSchema = Schema.ofType<T?>().map {
            it?.let { Json.encodeToString(serializersModule.serializer(), it) }
        }

        return field(readSchema, writeSchema)
    }
}
