package nl.helico.ktorize.importmap

import kotlinx.html.*
import nl.helico.ktorize.html.DeferredTagConsumer
import nl.helico.ktorize.html.Hook

class AddImportMapHook(
    val builder: ImportMapBuilder
): Hook.TagEnd {
    override fun beforeTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag): Boolean {
        if (tag !is HEAD) return true

        consumer.defer { downstream ->
            SCRIPT(initialAttributes = mapOf("type" to "importmap"), consumer = downstream)
                .visit {
                    unsafe { +builder.build().toString() }
                }
        }

        return true
    }

    override fun afterTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag) {
    }
}