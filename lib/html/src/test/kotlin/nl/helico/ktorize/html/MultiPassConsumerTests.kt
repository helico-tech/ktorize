package nl.helico.ktorize.html

import kotlinx.html.*
import kotlinx.html.stream.HTMLStreamBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.expect

class MultiPassConsumerTests {

    @Test
    fun simple() {
        val consumer = MultiPassConsumer()
        consumer.html {
            head {
                title { +"Hello" }
            }
            body {
                h1 { +"World" }
            }
        }

        val htmlBuilder  = HTMLStreamBuilder(StringBuilder(), prettyPrint = false, xhtmlCompatible = false)

        val downstream = DownstreamRenderPass(htmlBuilder)

        consumer.applyRenderPass(downstream)

        val result = htmlBuilder.out.toString()

        assertEquals("<html><head><title>Hello</title></head><body><h1>World</h1></body></html>", result)
    }

    @Test
    fun doubleFinalize() {
        val consumer = MultiPassConsumer()
        consumer.apply {
            html {  }
            html {  }
        }

        val htmlBuilder  = HTMLStreamBuilder(StringBuilder(), prettyPrint = false, xhtmlCompatible = false)

        val downstream = DownstreamRenderPass(htmlBuilder)

        consumer.applyRenderPass(downstream)

        val result = htmlBuilder.out.toString()

        assertEquals("<html></html><html></html>", result)
    }
}