package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class FieldDelegate<T>(
    val type: FieldType<T>,
    val parametersBuilder: ParametersBuilder,
    val property: KProperty<*>
): ReadWriteProperty<Form, T> {

    override fun getValue(thisRef: Form, property: KProperty<*>): T {
        return type.read(parametersBuilder[property.name])
    }

    override fun setValue(thisRef: Form, property: KProperty<*>, value: T) {
        val result = type.write(value)
        if (result != null) {
            parametersBuilder[property.name] = result
        } else {
            parametersBuilder.remove(property.name)
        }
    }
}