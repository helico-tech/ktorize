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
    view: HTMLView
) {
    val text = buildDeferredHTML(prettyPrint, xhtmlCompatible, hooks, view)
    respond(TextContent(text, ContentType.Text.Html.withCharset(Charsets.UTF_8), status))
}

suspend fun ApplicationCall.respondHtmlFragment(
    status: HttpStatusCode = HttpStatusCode.OK,
    prettyPrint: Boolean = true,
    xhtmlCompatible: Boolean = false,
    hooks: List<Hook> = this.renderingPipeline.hooks,
    view: HTMLFragment
) {
    val text = buildDeferredHTMLFragment(prettyPrint, xhtmlCompatible, hooks, {}, view)
    respond(TextContent(text, ContentType.Text.Html.withCharset(Charsets.UTF_8), status))
}