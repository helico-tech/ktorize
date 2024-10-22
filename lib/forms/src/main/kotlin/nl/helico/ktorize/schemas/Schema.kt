package nl.helico.ktorize.schemas

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

interface Schema<T> {
    fun parse(raw: Any?): T

    companion object {

        inline fun <reified T> ofType(): Schema<T?> = object : Schema<T?> {
            override fun parse(raw: Any?): T? = when (raw) {
                is T -> raw
                null -> null
                else -> throw IllegalArgumentException("Expected ${T::class.simpleName}, but got ${raw.javaClass.simpleName}")
            }
        }

        fun <T> either(vararg schemas: Schema<T>): Schema<T> = object : Schema<T> {
            override fun parse(raw: Any?): T  {
                for (schema in schemas) {
                    try {
                        return schema.parse(raw)
                    } catch (e: IllegalArgumentException) {
                        // Ignore
                    }
                }
                throw IllegalArgumentException("No schema matched")
            }
        }
    }
}

fun <I, O> Schema<I>.map(transform: (I) -> O): Schema<O> = object : Schema<O> {
    override fun parse(raw: Any?): O = this@map.parse(raw).let(transform)
}

@JvmName("deserializeNullable")
fun <T> Schema<String?>.deserialize(stringFormat: StringFormat = Json, serializer: KSerializer<T>) = map { data ->
    data?.let { stringFormat.decodeFromString(serializer, it) }
}

@JvmName("deserialize")
fun <T> Schema<String>.deserialize(stringFormat: StringFormat = Json, serializer: KSerializer<T>) = map { data ->
    stringFormat.decodeFromString(serializer, data)
}

@Suppress("UNCHECKED_CAST")
@JvmName("deserializeNullable")
inline fun <reified T> Schema<String?>.deserialize(stringFormat: StringFormat, serializersModule: SerializersModule): Schema<T?> {
    return when (T::class) {
        String::class -> this as Schema<T?>
        else -> deserialize(stringFormat = stringFormat, serializer = serializersModule.serializer())
    }
}

@Suppress("UNCHECKED_CAST")
@JvmName("deserialize")
inline fun <reified T> Schema<String>.deserialize(stringFormat: StringFormat, serializersModule: SerializersModule): Schema<T> {
    return when (T::class) {
        String::class -> this as Schema<T>
        else -> deserialize(stringFormat = stringFormat, serializer = serializersModule.serializer())
    }
}

@JvmName("deserializeNullable")
inline fun <reified T> Schema<String?>.deserialize() = deserialize<T>(stringFormat = Json, serializersModule = EmptySerializersModule())

@JvmName("deserialize")
inline fun <reified T> Schema<String>.deserialize() = deserialize<T>(stringFormat = Json, serializersModule = EmptySerializersModule())

fun <T> Schema<T?>.notNull(): Schema<T> = object : Schema<T> {
    override fun parse(raw: Any?): T = when (val value = this@notNull.parse(raw)) {
        null -> throw IllegalArgumentException("Expected non-null value")
        else -> value
    }
}

fun <T> Schema<T?>.default(default: T): Schema<T> = object : Schema<T> {
    override fun parse(raw: Any?): T = when (val value = this@default.parse(raw)) {
        null -> default
        else -> value
    }
}



