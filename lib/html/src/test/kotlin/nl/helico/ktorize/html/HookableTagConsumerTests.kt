package nl.helico.ktorize.html

import kotlinx.html.*
import kotlinx.html.stream.HTMLStreamBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class HookableTagConsumerTests {

    fun consumer(hooks: List<Hook>) = HookableTagConsumer(
        hooks = hooks,
        downstream = DeferredTagConsumer(
            downstream = HTMLStreamBuilder(
                out = StringBuilder(),
                prettyPrint = false,
                xhtmlCompatible = false
            )
        )
    )

    @Test
    fun noHooks() {
        val result = consumer(emptyList()).html {
            head {
                title { +"Hello" }
            }
            body {
                h1 { +"World" }
            }
        }.toString()

        assertEquals("<html><head><title>Hello</title></head><body><h1>World</h1></body></html>", result)
    }

    @Test
    fun injectCustomScriptHook() {
        val hook = object : Hook.TagEnd {
            override fun beforeTagEnd(consumer: TagConsumer<*>, tag: Tag): Boolean {
                (tag as? HEAD)?.script(src = "custom.js") {}
                return true
            }

            override fun afterTagEnd(consumer: TagConsumer<*>, tag: Tag) {}
        }

        val result = consumer(listOf(hook)).html {
            head {
                title { +"Hello" }
            }
            body {
                h1 { +"World" }
            }
        }.toString()

        assertEquals("<html><head><title>Hello</title><script src=\"custom.js\"></script></head><body><h1>World</h1></body></html>", result)
    }
}