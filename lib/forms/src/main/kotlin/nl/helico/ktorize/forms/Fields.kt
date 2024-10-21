package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Field<T> : ReadWriteProperty<Form, T>, PropertyDelegateProvider<Form, Field<T>> {
    val property: KProperty<*>
    val name get() = property.name
}

class ScalarDelegate<T>(
    private val parametersBuilder: ParametersBuilder,
    private val serializer: KSerializer<T>,
    private val register: (Field<*>) -> Unit
): Field<T?> {

    override lateinit var property: KProperty<*>

    override fun provideDelegate(thisRef: Form, property: KProperty<*>): Field<T?> {
        this.property = property
        register(this)
        return this
    }

    override fun getValue(thisRef: Form, property: KProperty<*>): T? {
        return parametersBuilder[name]?.let {
            Json.decodeFromString(serializer, it)
        }
    }

    override fun setValue(thisRef: Form, property: KProperty<*>, value: T?) {
        if (value == null) {
            parametersBuilder.remove(name)
        } else {
            parametersBuilder[name] = Json.encodeToString(serializer, value)
        }
    }

    fun default(default: () -> T): Field<T> {
        return DefaultDelegate(this, default, register)
    }

    fun default(default: T): Field<T> {
        return DefaultDelegate(this, { default }, register)
    }
}

class DefaultDelegate<T>(
    private val inner: Field<T?>,
    private val defaultValue: () -> T,
    private val register: (Field<T>) -> Unit
) : Field<T> {
    override val property: KProperty<*> get() = inner.property

    override fun provideDelegate(thisRef: Form, property: KProperty<*>): Field<T> {
        inner.provideDelegate(thisRef, property)
        register(this)
        return this
    }

    override fun getValue(thisRef: Form, property: KProperty<*>): T {
        if (inner.getValue(thisRef, property) == null) {
            inner.setValue(thisRef, property, defaultValue())
        }
        return inner.getValue(thisRef, property)!!
    }

    override fun setValue(thisRef: Form, property: KProperty<*>, value: T) {
        inner.setValue(thisRef, property, value)
    }
}