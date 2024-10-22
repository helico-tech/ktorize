package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.helico.ktorize.schemas.Schema
import nl.helico.ktorize.schemas.deserialize
import nl.helico.ktorize.schemas.map
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Form(
    initialParameters: Parameters = Parameters.Empty,
    val serializersModule: SerializersModule = Json.serializersModule
) {
    val parametersBuilder = ParametersBuilder().apply { appendAll(initialParameters) }

    private val delegates = mutableMapOf<String, FieldDelegate<*>>()

    inline operator fun <reified T> Schema<T>.provideDelegate(thisRef: Form, property: KProperty<*>): ReadWriteProperty<Form, T> {
        val writeSchema = Schema.ofType<T?>().map {
            when (it) {
                is String? -> it
                else -> Json.encodeToString(serializersModule.serializer(), it)
            }
        }
        val fieldType = FieldType(this, writeSchema)
        val delegate = FieldDelegate(fieldType, property)

        registerDelegate(delegate)

        return delegate
    }

    fun registerDelegate(delegate: FieldDelegate<*>) {
        delegates[delegate.property.name] = delegate
    }

    inline fun <reified T> field(): Schema<T?> = Schema.ofType<String>().deserialize()

    fun validate(): Map<KProperty<*>, Exception> {
        val results = mutableMapOf<KProperty<*>, Exception>()

        for ((_, delegate) in delegates) {
            try {
                delegate.getValue(this, delegate.property)
            } catch (e: Exception) {
                results[delegate.property] = e
            }
        }
        return results
    }
}
