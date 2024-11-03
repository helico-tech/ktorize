import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.io.asSource
import kotlinx.io.buffered

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }

        get("/resource/{name}") {
            val name = call.parameters["name"]!!
            val source = environment.classLoader.getResourceAsStream("asset/$name")!!.asSource().buffered()
            val contentType = ContentType.defaultForFilePath(name)

            call.respondSource(source, contentType)
        }
    }
}