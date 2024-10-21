package nl.helico.ktorize.forms

import io.ktor.http.*
import nl.helico.ktorize.schemas.Schema
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Field<T> : ReadWriteProperty<Form, T>, PropertyDelegateProvider<Form, Field<T>> {
    val property: KProperty<*>
    val name get() = property.name
}

class SchemaBasedField<T>(
    val readSchema: Schema<T>,
    val writeSchema: Schema<String?>,
    val parametersBuilder: ParametersBuilder,
    val onProvided: (Field<T>) -> Unit
) : Field<T> {

    override lateinit var property: KProperty<*>

    override fun provideDelegate(thisRef: Form, property: KProperty<*>): Field<T> {
        this.property = property
        onProvided(this)
        return this
    }

    override fun getValue(thisRef: Form, property: KProperty<*>): T {
        return readSchema.parse(parametersBuilder[property.name])
    }

    override fun setValue(thisRef: Form, property: KProperty<*>, value: T) {
        val result = writeSchema.parse(value)
        if (result != null) {
            parametersBuilder[property.name] = result
        } else {
            parametersBuilder.remove(property.name)
        }
    }
}