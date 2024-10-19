package nl.helico.ktorize.html

import kotlinx.html.TagConsumer
import kotlinx.html.stream.HTMLStreamBuilder

fun buildDeferredHTML(
    prettyPrint: Boolean = false,
    xhtmlCompatible: Boolean = false,
    hooks: List<Hook> = emptyList(),
    block: TagConsumer<*>.() -> Unit
): String {
    val downstream = StringBuilder()

    val htmlBuilder = HTMLStreamBuilder(
        out = downstream,
        prettyPrint = prettyPrint,
        xhtmlCompatible = xhtmlCompatible
    )

    val consumer = HookableTagConsumer(
        hooks = hooks,
        downstream = DeferredTagConsumer(
            downstream = htmlBuilder
        )
    )

    downstream.append("<!DOCTYPE html>\n")

    consumer.block()

    return downstream.toString()
}