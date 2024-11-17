package nl.helico.ktorize.html

import kotlinx.html.Entities
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.Unsafe
import kotlinx.html.org.w3c.dom.events.Event

sealed interface TagConsumerAction {
    data class TagAttributeChange(val tag: Tag, val attribute: String, val value: String?) : TagConsumerAction
    data class TagComment(val content: CharSequence) : TagConsumerAction
    data class TagContent(val content: CharSequence) : TagConsumerAction
    data class TagContentEntity(val entity: Entities) : TagConsumerAction
    data class TagContentUnsafe(val block: Unsafe.() -> Unit) : TagConsumerAction
    data class TagEnd(val tag: Tag) : TagConsumerAction
    data class TagEvent(val tag: Tag, val event: String, val value: (Event) -> Unit) : TagConsumerAction
    data class TagStart(val tag: Tag) : TagConsumerAction
    data class Deferred(val block: TagConsumer<*>.() -> Unit) : TagConsumerAction
    data object Finalize : TagConsumerAction
}

fun TagConsumerAction.apply(tagConsumer: TagConsumer<*>) {
    when (this) {
        is TagConsumerAction.TagAttributeChange -> tagConsumer.onTagAttributeChange(tag, attribute, value)
        is TagConsumerAction.TagComment -> tagConsumer.onTagComment(content)
        is TagConsumerAction.TagContent -> tagConsumer.onTagContent(content)
        is TagConsumerAction.TagContentEntity -> tagConsumer.onTagContentEntity(entity)
        is TagConsumerAction.TagContentUnsafe -> tagConsumer.onTagContentUnsafe(block)
        is TagConsumerAction.TagEnd -> tagConsumer.onTagEnd(tag)
        is TagConsumerAction.TagEvent -> tagConsumer.onTagEvent(tag, event, value)
        is TagConsumerAction.TagStart -> tagConsumer.onTagStart(tag)
        is TagConsumerAction.Finalize -> tagConsumer.finalize()
        is TagConsumerAction.Deferred -> when (tagConsumer) {
            is DeferredTagConsumer<*> -> tagConsumer.onDeferred(block)
            else -> this.block(tagConsumer)
        }
    }
}