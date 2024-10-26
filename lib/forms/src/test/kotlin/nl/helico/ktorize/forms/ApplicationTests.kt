package nl.helico.ktorize.forms

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import nl.helico.ktorize.schemas.notNull
import kotlin.test.Test

class ApplicationTests {

    @Test
    fun fieldString() = testApplication {
        application {
            routing {
                post("/") {
                    val form = call.receiveForm {
                       object : Form() {
                           var name by field<String>().notNull()
                           var lastName by field<String>()
                       }
                    }

                    call.respondText("Hello, ${form.name} ${form.lastName ?: ""}")
                }
            }
        }

        val response = client.post("/") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("name" to "John").formUrlEncode())
        }

        assert(response.bodyAsText() == "Hello, John ")
    }
}