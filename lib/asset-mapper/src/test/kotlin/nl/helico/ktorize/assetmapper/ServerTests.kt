package nl.helico.ktorize.assetmapper

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerTests {

    private fun ApplicationTestBuilder.setup(setup: AssetMapperConfiguration.() -> Unit = {}) {
        install(AssetMapperPlugin) {
            setup()
        }
    }

    @Test
    fun simpleAssetTest() = testApplication {
        setup {
            basePackage = "server-tests/simple-asset"
        }

        var response = client.get("/assets/my-test-asset.0ebb698246629183e0404f3b62691205.txt")
        var content = response.bodyAsText()
        assertEquals("My contents", content)

        response = client.get("/assets/sub-folder/sub-folder-asset.674838e9c1da422af1a682eee073fb65.txt")
        content = response.bodyAsText()
        assertEquals("Sub folder", content)
    }
}