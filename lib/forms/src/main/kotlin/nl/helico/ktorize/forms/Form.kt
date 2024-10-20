package nl.helico.ktorize.forms

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

abstract class Form(
    initialParameters: Parameters = Parameters.Empty,
    val serializersModule: SerializersModule = Json.serializersModule
) {
    private val parametersBuilder = ParametersBuilder().apply { appendAll(initialParameters) }

    fun <T : Any?> scalar(serializer: KSerializer<T>) = ScalarDelegate(parametersBuilder, serializer)

    inline fun <reified T : Any?> scalar() = scalar<T>(serializersModule.serializer())
}
