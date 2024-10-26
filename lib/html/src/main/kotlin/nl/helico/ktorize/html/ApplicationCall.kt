package nl.helico.ktorize.html

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.html.HTML
import kotlinx.html.TagConsumer

suspend fun ApplicationCall.respondHtml(
    status: HttpStatusCode = HttpStatusCode.OK,
    prettyPrint: Boolean = true,
    xhtmlCompatible: Boolean = false,
    hooks: List<Hook> = this.renderingPipeline.hooks,
    block: HTML.() -> Unit
) {
    val text = buildDeferredHTML(prettyPrint, xhtmlCompatible, hooks, block)
    respond(TextContent(text, ContentType.Text.Html.withCharset(Charsets.UTF_8), status))
}

suspend fun ApplicationCall.respondHtmlFragment(
    status: HttpStatusCode = HttpStatusCode.OK,
    prettyPrint: Boolean = true,
    xhtmlCompatible: Boolean = false,
    hooks: List<Hook> = this.renderingPipeline.hooks,
    block: TagConsumer<*>.() -> Unit
) {
    val text = buildDeferredHTMLFragment(prettyPrint, xhtmlCompatible, hooks, {}, block)
    respond(TextContent(text, ContentType.Text.Html.withCharset(Charsets.UTF_8), status))
}