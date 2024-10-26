package nl.helico.ktorize.hotwire.turbo

import kotlinx.html.*


@HtmlTagMarker
fun HEAD.enableViewTransition() {
    meta(name = "view-transition", content = "same-origin")
}

@HtmlTagMarker
fun HEAD.turboVisitControlReload() {
    meta(name = "turbo-visit-control", content = "reload")
}

@HtmlTagMarker
fun HEAD.turboRoot(url: String) {
    meta(name = "turbo-root", content = url)
}

@HtmlTagMarker
fun HEAD.turboPrefetch(boolean: Boolean = true) {
    meta(name = "turbo-prefetch", content = boolean.toString())
}

@HtmlTagMarker
fun HEAD.turboRefreshMethod(method: TurboRefreshMethod) {
    meta(name = "turbo-refresh-method", content = method.value)
}

@HtmlTagMarker
fun HEAD.turboRefreshScroll(type: TurboRefreshScrollType) {
    meta(name = "turbo-refresh-scroll", content = type.value)
}

@HtmlTagMarker
fun HEAD.csrfToken(token: String) {
    meta(name = "csrf-token", content = token)
}

@HtmlTagMarker
fun FlowContent.turbo(boolean: Boolean) {
    attributes["data-turbo"] = boolean.toString()
}

@HtmlTagMarker
fun FlowContent.turboPrefetch(boolean: Boolean = true) {
    attributes["data-turbo-prefetch"] = boolean.toString()
}

@HtmlTagMarker
fun FlowContent.turboPermanent(boolean: Boolean = true) {
    attributes["data-turbo-permanent"] = boolean.toString()
}

@HtmlTagMarker
fun A.turboAction(type: TurboActionType) {
    attributes["data-turbo-action"] = type.value
}

@HtmlTagMarker
fun A.turboMethod(method: TurboMethod) {
    attributes["data-turbo-method"] = method.value
}

@HtmlTagMarker
fun A.turboConfirm(message: String) {
    attributes["data-turbo-confirm"] = message
}

@HtmlTagMarker
fun A.turboPreload(boolean: Boolean = true) {
    attributes["data-turbo-preload"] = boolean.toString()
}

@HtmlTagMarker
fun SCRIPT.turboTrackReload() {
    attributes["data-turbo-track"] = "reload"
}

@HtmlTagMarker
fun LINK.turboTrackReload() {
    attributes["data-turbo-track"] = "reload"
}

@HtmlTagMarker
fun A.turboFrame(target: String) {
    attributes["data-turbo-frame"] = target
}

@HtmlTagMarker
fun FORM.turboFrame(target: String) {
    attributes["data-turbo-frame"] = target
}

@HtmlTagMarker
fun TURBOFRAME.turboAction(type: TurboActionType) {
    attributes["data-turbo-action"] = type.value
}

@HtmlTagMarker
fun TURBOFRAME.turboRefresh(method: TurboRefreshMethod) {
    attributes["refresh"] = method.value
}

@HtmlTagMarker
fun FlowContent.TurboFrame(
    id: String,
    src: String? = null,
    lazy: Boolean? = null,
    target: String? = null,
    block: TURBOFRAME.() -> Unit = {}
): Unit =  TURBOFRAME(attributesMapOf("id", id, "src", src, "lazy", lazy?.toString(), "target", target), consumer).visit(block)

@HtmlTagMarker
fun FlowContent.TurboStreamSource(
    src: String
) = TURBOSTREAMSOURCE(attributesMapOf("src", src), consumer).visit {  }

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.TurboStream(
    action: TurboStreamActionType,
    target: String? = null,
    targets: String? = null,
    block: TEMPLATE.() -> Unit
) = TURBOSTREAM(attributesMapOf("action", action.value, "target", target, "targets", targets), this).visitAndFinalize(this) {
    template(block)
}