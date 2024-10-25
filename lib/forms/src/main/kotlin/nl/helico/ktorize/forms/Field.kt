package nl.helico.ktorize.forms

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class SingleDelegate<T>(
    val type: FieldType<T>,
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

class MultipleDelegate<T>(
    val type: FieldType<T>
) : ReadWriteProperty<Form, List<T>> {
    override fun getValue(thisRef: Form, property: KProperty<*>): List<T> {
        return thisRef.parametersBuilder.getAll(property.name)?.map { type.read(it) } ?: emptyList()
    }

    override fun setValue(thisRef: Form, property: KProperty<*>, value: List<T>) {
        thisRef.parametersBuilder.remove(property.name)
        value.forEach {
            val result = type.write(it)
            if (result != null) {
                thisRef.parametersBuilder.append(property.name, result)
            }
        }
    }
}