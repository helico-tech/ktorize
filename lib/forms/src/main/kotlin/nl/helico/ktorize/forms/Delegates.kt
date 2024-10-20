package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty

interface FormDelegate<T> {
    operator fun getValue(thisRef: Form, property: KProperty<*>): T
    operator fun setValue(thisRef: Form, property: KProperty<*>, value: T)
}


class ScalarDelegate<T : Any?>(
    private val parametersBuilder: ParametersBuilder,
    private val serializer: KSerializer<T>
): FormDelegate<T?> {

    override operator fun getValue(thisRef: Form, property: KProperty<*>): T?  {
        return parametersBuilder[property.name]?.let {
            Json.decodeFromString(serializer, it)
        }
    }

    override operator fun setValue(thisRef: Form, property: KProperty<*>, value: T?) {
        if (value != null) {
            parametersBuilder[property.name] = Json.encodeToString(serializer, value)
        } else {
            parametersBuilder.remove(property.name)
        }
    }
}

class DefaultDelegate<T : Any?>(
    private val downstream: FormDelegate<T?>,
    private val defaultValue: T
): FormDelegate<T> {
    override operator fun getValue(thisRef: Form, property: KProperty<*>): T {
        if (downstream.getValue(thisRef, property) == null) {
            downstream.setValue(thisRef, property, defaultValue)
        }
        return downstream.getValue(thisRef, property)!!
    }

    override operator fun setValue(thisRef: Form, property: KProperty<*>, value: T) {
        downstream.setValue(thisRef, property, value)
    }
}

class ValidationDelegate<T : Any?>(
    private val downstream: FormDelegate<T>,
    private val validator: (T) -> Boolean
): FormDelegate<T> {
    override operator fun getValue(thisRef: Form, property: KProperty<*>): T {
        val value = downstream.getValue(thisRef, property)
        require(validator(value)) { "Property `${property.name}` failed validation" }
        return value
    }

    override operator fun setValue(thisRef: Form, property: KProperty<*>, value: T) {
        require(validator(value)) { "Property `${property.name}` failed validation" }
        downstream.setValue(thisRef, property, value)
    }
}

class RequiredDelegate<T>(
    private val downstream: FormDelegate<T?>,
): FormDelegate<T> {
    override operator fun getValue(thisRef: Form, property: KProperty<*>): T {
        return requireNotNull(downstream.getValue(thisRef, property)) { "Property `${property.name}` is required" }
    }

    override operator fun setValue(thisRef: Form, property: KProperty<*>, value: T) {
        downstream.setValue(thisRef, property, value)
    }
}

fun <T> FormDelegate<T?>.default(defaultValue: T): FormDelegate<T> {
    return DefaultDelegate(this, defaultValue)
}

fun <T : Any?> FormDelegate<T>.validate(validator: (T) -> Boolean): FormDelegate<T> {
    return ValidationDelegate(this, validator)
}

fun <T> FormDelegate<T?>.required(): FormDelegate<T> {
    return RequiredDelegate(this)
}