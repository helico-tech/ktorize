package nl.bumastemra.portal

import io.ktor.server.application.*
import io.ktor.util.*
import org.kodein.di.DI

internal val DI = AttributeKey<DI>("DI")

fun Application.DI(
    vararg modules: DI.Module
) {
    val di = DI {
        modules.forEach { import(it) }
    }

    attributes.put(nl.bumastemra.portal.DI, di)
}

val Application.DI: DI get() = attributes[nl.bumastemra.portal.DI]