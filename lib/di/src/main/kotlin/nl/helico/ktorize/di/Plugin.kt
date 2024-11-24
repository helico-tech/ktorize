package nl.helico.ktorize.di

import io.ktor.server.application.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.util.*
import org.kodein.di.DI
import org.kodein.di.DI.MainBuilder
import org.kodein.di.instance

internal val name = "DI"

val DIKey get() = AttributeKey<DI>(name)

fun DIPlugin(init: MainBuilder.() -> Unit) = createApplicationPlugin(name) {
    val di = DI(init = init)
    application.attributes.put(DIKey, di)
}


val Application.di: DI get() = attributes[DIKey]
val Route.di: DI get() = application.attributes[DIKey]

inline fun <reified T : Any> Application.instance(): T {
    val instance: T by di.instance<T>()
    return instance
}

inline fun <reified T : Any> Route.instance(): T {
    return application.instance<T>()
}