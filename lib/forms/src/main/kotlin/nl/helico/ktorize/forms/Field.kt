package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlin.reflect.KProperty


class Field<T>(
    val type: FieldType<T>,
    val parametersBuilder: ParametersBuilder,
    val onProvided: (Field<T>) -> Unit
)  {

    lateinit var property: KProperty<*>

    val name: String
        get() = property.name

    operator fun provideDelegate(thisRef: Form, property: KProperty<*>): Field<T> {
        this.property = property
        onProvided(this)
        return this
    }

    operator fun getValue(thisRef: Form, property: KProperty<*>): T {
        return type.read(parametersBuilder[property.name])
    }

    operator fun setValue(thisRef: Form, property: KProperty<*>, value: T) {
        val result = type.write(value)
        if (result != null) {
            parametersBuilder[property.name] = result
        } else {
            parametersBuilder.remove(property.name)
        }
    }
}