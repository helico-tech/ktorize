package nl.helico.ktorize.html

import kotlinx.html.HTML
import kotlinx.html.TagConsumer
import kotlinx.html.html
import kotlinx.html.stream.HTMLStreamBuilder

typealias HTMLFragment = TagConsumer<*>.() -> Unit
typealias HTMLView = HTML.() -> Unit

fun buildDeferredHTMLFragment(
    prettyPrint: Boolean = false,
    xhtmlCompatible: Boolean = false,
    hooks: List<Hook> = emptyList(),
    stringBuilderBlock: StringBuilder.() -> Unit = {},
    block: HTMLFragment
): String {
    val downstream = StringBuilder().also(stringBuilderBlock)

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

    consumer.block()

    return downstream.toString()
}

fun buildDeferredHTML(
    prettyPrint: Boolean = false,
    xhtmlCompatible: Boolean = false,
    hooks: List<Hook> = emptyList(),
    block: HTMLView
): String {
    return buildDeferredHTMLFragment(prettyPrint, xhtmlCompatible, hooks, stringBuilderBlock = {
        append("<!DOCTYPE html>")
    }) {
        html(block = block)
    }
}