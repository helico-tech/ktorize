package nl.helico.ktorize.assetmapper

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains

class PluginTests {

    @Test
    fun cssMappingTest() = testApplication {
        application {
            install(AssetMapperPlugin)
        }

        val response = client.get("/assets/test.css")

        val body = response.bodyAsText()

        assertContains(body, "@import url(\"/assets/file-1-d41d8cd98f00b204e9800998ecf8427e.css\");")
        assertContains(body, "@import url(\"/assets/file-2-d41d8cd98f00b204e9800998ecf8427e.css\");")
    }
}