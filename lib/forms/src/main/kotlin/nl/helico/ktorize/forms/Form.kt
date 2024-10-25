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

    private val delegates = mutableMapOf<KProperty<*>, SingleDelegate<*>>()

    inline operator fun <reified T> Schema<T>.provideDelegate(thisRef: Form, property: KProperty<*>): ReadWriteProperty<Form, T> {
        val writeSchema = Schema.ofType<T?>().map {
            when (it) {
                is String? -> it
                else -> Json.encodeToString(serializersModule.serializer(), it)
            }
        }
        val fieldType = FieldType(this, writeSchema)
        val delegate = SingleDelegate(fieldType)

        registerDelegate(delegate, property)

        return delegate
    }

    inline fun <reified T> Schema<T>.multiple(): ReadWriteProperty<Form, List<T>> {
        val writeSchema = Schema.ofType<T?>().map {
            when (it) {
                is String? -> it
                else -> Json.encodeToString(serializersModule.serializer(), it)
            }
        }
        val fieldType = FieldType(this, writeSchema)
        return MultipleDelegate(fieldType)
    }

    fun registerDelegate(delegate: SingleDelegate<*>, property: KProperty<*>) {
        delegates[property] = delegate
    }

    inline fun <reified T> field(): Schema<T?> = Schema.ofType<String>().deserialize()

    fun validate(): Map<KProperty<*>, Exception> {
        val results = mutableMapOf<KProperty<*>, Exception>()

        for ((property, delegate) in delegates) {
            try {
                delegate.getValue(this, property)
            } catch (e: Exception) {
                results[property] = e
            }
        }
        return results
    }

    fun hydrate(parameters: Parameters) {
        parametersBuilder.clear()
        parametersBuilder.appendAll(parameters)
    }

    companion object {
        fun interface Factory<F : Form> {
            fun create(): F
        }
    }
}
