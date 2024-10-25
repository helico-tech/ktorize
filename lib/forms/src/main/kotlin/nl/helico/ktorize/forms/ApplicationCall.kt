package nl.helico.ktorize.forms

import io.ktor.server.application.*
import io.ktor.server.request.*

suspend fun <F : Form> ApplicationCall.receiveForm(factory: Form.Companion.Factory<F>): F {
    val parameters = receiveParameters()
    return factory.create().also {
        it.hydrate(parameters)
    }
}

suspend fun <F : Form> ApplicationCall.receiveForm(builder: () -> F): F {
    val factory = object : Form.Companion.Factory<F> {
        override fun create(): F = builder().also {
            it.hydrate(parameters)
        }
    }

    return receiveForm(factory)
}