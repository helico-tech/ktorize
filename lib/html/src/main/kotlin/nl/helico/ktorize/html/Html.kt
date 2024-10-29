package nl.helico.ktorize.html

import kotlinx.html.HTML
import kotlinx.html.TagConsumer
import kotlinx.html.consumers.delayed
import kotlinx.html.html
import kotlinx.html.stream.HTMLStreamBuilder

typealias HTMLFragment = TagConsumer<*>.() -> Unit
typealias HTMLView = HTML.() -> Unit

fun buildDeferredHTMLFragment(
    prettyPrint: Boolean = false,
    xhtmlCompatible: Boolean = false,
    renderPasses: List<RenderPass> = emptyList(),
    stringBuilderBlock: StringBuilder.() -> Unit = {},
    block: HTMLFragment
): String {
    val downstream = StringBuilder().also(stringBuilderBlock)

    val downstreamRenderPass = DownstreamRenderPass(
        downstream = HTMLStreamBuilder(
            out = downstream,
            prettyPrint = prettyPrint,
            xhtmlCompatible = xhtmlCompatible
        ).delayed()
    )

    val consumer = MultiPassConsumer()

    consumer.block()

    renderPasses.forEach { renderPass ->
        consumer.applyRenderPass(renderPass)
    }

    consumer.applyRenderPass(downstreamRenderPass)

    return downstream.toString()
}

fun buildDeferredHTML(
    prettyPrint: Boolean = false,
    xhtmlCompatible: Boolean = false,
    renderPasses: List<RenderPass> = emptyList(),
    block: HTMLView
): String {
    return buildDeferredHTMLFragment(prettyPrint, xhtmlCompatible, renderPasses, stringBuilderBlock = {
        append("<!DOCTYPE html>")
    }) {
        html(block = block)
    }
}