package nl.helico.ktorize.hotwire.turbo

import kotlinx.html.*

sealed class TurboMethod(val value: String) {
    data object Get : TurboMethod("get")
    data object Post : TurboMethod("post")
    data object Put : TurboMethod("put")
    data object Delete : TurboMethod("delete")
    data object Patch : TurboMethod("patch")
}

sealed class TurboActionType(val value: String) {
    data object Replace : TurboActionType("replace")
    data object Advance : TurboActionType("advance")
}

sealed class TurboRefreshMethod(val value: String) {
    data object Morph : TurboRefreshMethod("morph")
    data object Replace : TurboRefreshMethod("replace")
}

sealed class TurboRefreshScrollType(val value: String) {
    data object Preserve : TurboRefreshScrollType("preserve")
    data object Reset : TurboRefreshScrollType("reset")
}

sealed class TurboStreamActionType(val value: String) {
    data object Append : TurboStreamActionType("append")
    data object Prepend : TurboStreamActionType("prepend")
    data object Replace : TurboStreamActionType("replace")
    data object Update : TurboStreamActionType("update")
    data object Remove : TurboStreamActionType("remove")
    data object Before : TurboStreamActionType("before")
    data object After : TurboStreamActionType("after")
    data object Morph : TurboStreamActionType("morph")
    data object Refresh : TurboStreamActionType("refresh")
}

class TEMPLATE(
    initialAttributes : Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>
) :
    HTMLTag(
        tagName = "template",
        consumer = consumer,
        initialAttributes = initialAttributes,
        inlineTag = false,
        emptyTag = false
    ), HtmlBlockTag

class TURBOFRAME(
    initialAttributes : Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>
) :
    HTMLTag(
        tagName = "turbo-frame",
        consumer = consumer,
        initialAttributes = initialAttributes,
        inlineTag = false,
        emptyTag = false
    ), HtmlBlockTag

class TURBOSTREAM(
    initialAttributes : Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>
) :
    HTMLTag(
        tagName = "turbo-stream",
        consumer = consumer,
        initialAttributes = initialAttributes,
        inlineTag = false,
        emptyTag = false
    ), HtmlBlockTag

class TURBOSTREAMSOURCE(
    initialAttributes : Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>
) :
    HTMLTag(
        tagName = "turbo-stream-source",
        consumer = consumer,
        initialAttributes = initialAttributes,
        inlineTag = true,
        emptyTag = false,
    ), HtmlBlockTag

fun FlowContent.template(
    block: TEMPLATE.() -> Unit
) = TEMPLATE(consumer = consumer).visit(block)