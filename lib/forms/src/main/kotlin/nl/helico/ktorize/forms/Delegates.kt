package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty


class ScalarDelegate<T : Any?>(
    private val parametersBuilder: ParametersBuilder,
    private val serializer: KSerializer<T>
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T?  {
        return parametersBuilder[property.name]?.let {
            Json.decodeFromString(serializer, it)
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value != null) {
            parametersBuilder[property.name] = Json.encodeToString(serializer, value)
        } else {
            parametersBuilder.remove(property.name)
        }
    }

    fun withDefault(defaultValue: T): DefaultScalarDelegate<T> {
        return DefaultScalarDelegate(this, defaultValue)
    }
}

class DefaultScalarDelegate<T : Any?>(
    private val scalarDelegate: ScalarDelegate<T>,
    private val defaultValue: T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (scalarDelegate.getValue(thisRef, property) == null) {
            scalarDelegate.setValue(thisRef, property, defaultValue)
        }
        return scalarDelegate.getValue(thisRef, property)!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        scalarDelegate.setValue(thisRef, property, value)
    }
}