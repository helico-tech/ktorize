package nl.helico.ktorize.assetmapper

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlin.test.Test

class PluginTests {

    @Test
    fun onCallResponse() = testApplication {
        application {
            install(AssetMapperPlugin)
        }

        val response = client.get("/assets/test.css")

        val body = response.bodyAsText()

        println(body)
    }
}