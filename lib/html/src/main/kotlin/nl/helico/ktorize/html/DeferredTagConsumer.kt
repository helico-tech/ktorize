package nl.helico.ktorize.html

import kotlinx.html.Entities
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.Unsafe
import kotlinx.html.org.w3c.dom.events.Event

class DeferredTagConsumer<O>(
    val downstream: TagConsumer<O>
) : TagConsumer<O> {

    private val deferred = mutableListOf<(TagConsumer<*>) -> Unit>()

    fun defer(block: (TagConsumer<*>) -> Unit) {
        deferred.add(block)
    }

    override fun finalize(): O {
        deferred.forEach { it(downstream) }
        return downstream.finalize()
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        // do nothing since we defer by default
    }

    override fun onTagComment(content: CharSequence) {
        deferred.add { it.onTagComment(content) }
    }

    override fun onTagContent(content: CharSequence) {
        deferred.add { it.onTagContent(content) }
    }

    override fun onTagContentEntity(entity: Entities) {
        deferred.add { it.onTagContentEntity(entity) }
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        deferred.add { it.onTagContentUnsafe(block) }
    }

    override fun onTagEnd(tag: Tag) {
        deferred.add { it.onTagEnd(tag) }
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        deferred.add { it.onTagEvent(tag, event, value) }
    }

    override fun onTagStart(tag: Tag) {
        deferred.add { it.onTagStart(tag) }
    }
}