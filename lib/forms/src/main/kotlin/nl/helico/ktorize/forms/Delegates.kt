package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty

class ParameterDelegate<T : Any?>(
    private val parameterBuilder: ParametersBuilder,
    private val serializer: KSerializer<T>
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val values = parameterBuilder.getAll(property.name) ?: return null
        require(values.size == 1) { "Expected a single value for $property, but got $values" }

        val value = values.first()
        return Json.decodeFromString(serializer, value)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value == null) {
            parameterBuilder.remove(property.name)
        } else {
            parameterBuilder[property.name] = Json.encodeToString(serializer, value)
        }
    }
}