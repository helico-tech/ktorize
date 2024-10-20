package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

sealed interface Field {
    val name: String
}

class Scalar<T : Any?>(
    override val name: String,
    private val parametersBuilder: ParametersBuilder,
    private val serializer: KSerializer<T>,
) : Field {

    fun getOrNull(): T? {
        return parametersBuilder[name]?.let {
            Json.decodeFromString(serializer, it)
        }
    }

    fun set(value: T?) {
        if (value != null) {
            parametersBuilder.set(name, Json.encodeToString(serializer, value))
        } else {
            parametersBuilder.remove(name)
        }
    }
}

data class DefaultScalar<T : Any?>(
    private val inner: Scalar<T>,
    private val defaultValue: T,
): Field by inner {

    init {
        enforceDefaultValue()
    }

    fun get(): T {
        enforceDefaultValue()
        return inner.getOrNull()!!
    }

    fun set(value: T) {
        inner.set(value)
    }

    private fun enforceDefaultValue() {
        if (inner.getOrNull() == null) {
            inner.set(defaultValue)
        }
    }
}