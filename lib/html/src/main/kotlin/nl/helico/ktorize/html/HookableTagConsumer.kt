package nl.helico.ktorize.html

import io.ktor.util.*
import kotlinx.html.Entities
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.Unsafe
import kotlinx.html.org.w3c.dom.events.Event

class HookableTagConsumer<O>(
    val hooks: List<Hook>,
    val downstream: DeferredTagConsumer<O>,
) : TagConsumer<O> {

    companion object {
        val Hooks = AttributeKey<MutableList<Hook>>("HookableTagConsumer.Hooks")
    }

    override fun finalize(): O {
        applyHooks<Hook.Finalize>(
            before = { it.beforeFinalize(downstream); true },
            action = {},
            after = {}
        )

        return downstream.finalize()
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        downstream.onTagAttributeChange(tag, attribute, value)
    }

    override fun onTagComment(content: CharSequence) {
        applyHooks<Hook.Comment>(
            before = { it.beforeComment(downstream, content) },
            action = { it.onTagComment(content) },
            after = { it.afterComment(downstream, content) }
        )
    }

    override fun onTagContent(content: CharSequence) {
        applyHooks<Hook.Content>(
            before = { it.beforeContent(downstream, content) },
            action = { it.onTagContent(content) },
            after = { it.afterContent(downstream, content) }
        )
    }

    override fun onTagContentEntity(entity: Entities) {
        applyHooks<Hook.ContentEntity>(
            before = { it.beforeContentEntity(downstream, entity) },
            action = { it.onTagContentEntity(entity) },
            after = { it.afterContentEntity(downstream, entity) }
        )
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        applyHooks<Hook.ContentUnsafe>(
            before = { it.beforeContentUnsafe(downstream, block) },
            action = { it.onTagContentUnsafe(block) },
            after = { it.afterContentUnsafe(downstream, block) }
        )
    }

    override fun onTagEnd(tag: Tag) {
        applyHooks<Hook.TagEnd>(
            before = { it.beforeTagEnd(downstream, tag) },
            action = { it.onTagEnd(tag) },
            after = { it.afterTagEnd(downstream, tag) }
        )
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        downstream.onTagEvent(tag, event, value)
    }

    override fun onTagStart(tag: Tag) {
        applyHooks<Hook.TagStart>(
            before = { it.beforeTagStart(downstream, tag) },
            action = { it.onTagStart(tag) },
            after = { it.afterTagStart(downstream, tag) }
        )
    }

    private inline fun <reified H : Hook> applyHooks(
        crossinline before: (H) -> Boolean,
        crossinline action: (TagConsumer<*>) -> Unit,
        crossinline after: (H) -> Unit
    ) {
        val hooks = hooks.filterIsInstance<H>()

        val result = hooks.fold(true) { acc, hook ->
            acc && before(hook)
        }

        if (result) {
            action(downstream)
            hooks.forEach { after(it) }
        }
    }
}


