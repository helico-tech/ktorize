package nl.helico.ktorize.assetmapper

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ServerTests {

    private fun ApplicationTestBuilder.setup() {
        install(AssetMapperPlugin) {
            basePackage = "server-tests"
        }
    }

    @Test
    fun simpleAssetTest() = testApplication {
        setup()

        val response = client.get("/assets/my-test-asset.0ebb698246629183e0404f3b62691205.txt")
        println(response.bodyAsText())
    }
}