package nl.helico.ktorize.html

import kotlinx.html.Entities
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.Unsafe
import kotlinx.html.org.w3c.dom.events.Event

class MultiPassConsumer(): TagConsumer<Unit> {

    private var actions = ArrayDeque<TagConsumerAction>()

    override fun finalize() {
        add(TagConsumerAction.Finalize)
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        add(TagConsumerAction.TagAttributeChange(tag, attribute, value))
    }

    override fun onTagComment(content: CharSequence) {
        add(TagConsumerAction.TagComment(content))
    }

    override fun onTagContent(content: CharSequence) {
        add(TagConsumerAction.TagContent(content))
    }

    override fun onTagContentEntity(entity: Entities) {
        add(TagConsumerAction.TagContentEntity(entity))
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        add(TagConsumerAction.TagContentUnsafe(block))
    }

    override fun onTagEnd(tag: Tag) {
        add(TagConsumerAction.TagEnd(tag))
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        add(TagConsumerAction.TagEvent(tag, event, value))
    }

    override fun onTagStart(tag: Tag) {
        add(TagConsumerAction.TagStart(tag))
    }

    private fun add(action: TagConsumerAction) {
        if (actions.lastOrNull() == TagConsumerAction.Finalize) {
            actions.removeLast()
            actions.add(action)

            if (action !is TagConsumerAction.Finalize) {
                actions.add(TagConsumerAction.Finalize)
            }
        } else {
            actions.add(action)
        }
    }

    fun applyRenderPass(pass: RenderPass) {
        val current = actions
        actions = ArrayDeque()

        while (current.isNotEmpty()) {
            val action = current.removeFirst()

            if (pass.before(action, this)) {
                add(action)
            }

            pass.after(action, this)
        }
    }
}