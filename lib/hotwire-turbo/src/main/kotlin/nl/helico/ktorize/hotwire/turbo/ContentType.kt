package nl.helico.ktorize.hotwire.turbo

import io.ktor.http.*

val ContentType.Text.TurboStream get() = ContentType("text", "vnd.turbo-stream.html")