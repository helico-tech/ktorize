package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlin.reflect.KProperty


class ScalarDelegate<T : Any?>(
    private val parametersBuilder: ParametersBuilder,
    private val serializer: KSerializer<T>
) {
    operator fun getValue(thisRef: Form, property: KProperty<*>): Scalar<T>  {
        return Scalar(property.name, parametersBuilder, serializer)
    }

    fun withDefault(defaultValue: T): DefaultScalarDelegate<T> {
        return DefaultScalarDelegate(this, defaultValue)
    }
}

class DefaultScalarDelegate<T : Any?>(
    private val scalarDelegate: ScalarDelegate<T>,
    private val defaultValue: T
) {
    operator fun getValue(thisRef: Form, property: KProperty<*>): DefaultScalar<T> {
        return DefaultScalar(scalarDelegate.getValue(thisRef, property), defaultValue)
    }
}