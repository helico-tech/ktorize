import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AppTests {

    @Test
    fun index() = testApplication {
        application {
            module()
        }

        val response = client.get("/")
        assertEquals("Hello, World!", response.bodyAsText())
    }

    @Test
    fun resource() = testApplication {
        application {
            module()
        }

        val response = client.get("/resource/foo.txt")
        assertEquals("Foo", response.bodyAsText())
    }
}