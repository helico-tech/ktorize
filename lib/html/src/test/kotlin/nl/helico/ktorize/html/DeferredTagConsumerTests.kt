package nl.helico.ktorize.html

import kotlinx.html.*
import kotlinx.html.stream.HTMLStreamBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class DeferredTagConsumerTests {

    fun consumer() = DeferredTagConsumer(
        downstream = HTMLStreamBuilder(
            out = StringBuilder(),
            prettyPrint = false,
            xhtmlCompatible = false
        )
    )

    @Test
    fun simple() {
        val result = consumer().html {
            head {
                title { +"Hello" }
            }
            body {
                h1 { +"World" }
            }
        }.toString()

        assertEquals("<html><head><title>Hello</title></head><body><h1>World</h1></body></html>", result)
    }
}