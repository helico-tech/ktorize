package nl.helico.ktorize.hotwire.turbo

import io.ktor.http.*
import io.ktor.server.request.*

val ApplicationRequest.acceptsTurboStream: Boolean
    get() = acceptItems().any { ContentType.Text.TurboStream.toString() == it.value }