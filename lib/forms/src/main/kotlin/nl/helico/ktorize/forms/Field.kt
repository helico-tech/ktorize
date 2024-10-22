package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class FieldDelegate<T>(
    val type: FieldType<T>,
    val property: KProperty<*>
): ReadWriteProperty<Form, T> {

    override fun getValue(thisRef: Form, property: KProperty<*>): T {
        return type.read(thisRef.parametersBuilder[property.name])
    }

    override fun setValue(thisRef: Form, property: KProperty<*>, value: T) {
        val result = type.write(value)
        if (result != null) {
            thisRef.parametersBuilder[property.name] = result
        } else {
            thisRef.parametersBuilder.remove(property.name)
        }
    }
}