package nl.helico.ktorize.html

import kotlinx.html.*

interface Hook {
    interface Comment : Hook {
        fun beforeComment(consumer: TagConsumer<*>, content: CharSequence): Boolean
        fun afterComment(consumer: TagConsumer<*>, content: CharSequence)
    }

    interface Content : Hook {
        fun beforeContent(consumer: TagConsumer<*>, content: CharSequence): Boolean
        fun afterContent(consumer: TagConsumer<*>, content: CharSequence)
    }

    interface ContentEntity : Hook {
        fun beforeContentEntity(consumer: TagConsumer<*>, entity: Entities): Boolean
        fun afterContentEntity(consumer: TagConsumer<*>, entity: Entities)
    }

    interface ContentUnsafe : Hook {
        fun beforeContentUnsafe(consumer: TagConsumer<*>, block: Unsafe.() -> Unit): Boolean
        fun afterContentUnsafe(consumer: TagConsumer<*>, block: Unsafe.() -> Unit)
    }

    interface TagStart : Hook {
        fun beforeTagStart(consumer: TagConsumer<*>, tag: Tag): Boolean
        fun afterTagStart(consumer: TagConsumer<*>, tag: Tag)
    }

    interface TagEnd : Hook {
        fun beforeTagEnd(consumer: TagConsumer<*>, tag: Tag): Boolean
        fun afterTagEnd(consumer: TagConsumer<*>, tag: Tag)
    }

    interface Finalize : Hook {
        fun beforeFinalize(consumer: TagConsumer<*>)
    }
}

class AddScriptHook(
    val type: String? = null,
    val src: String? = null,
    val crossorigin: ScriptCrossorigin? = null,
    val block: SCRIPT.() -> Unit = {}
) : Hook.TagEnd {
    override fun beforeTagEnd(consumer: TagConsumer<*>, tag: Tag): Boolean {
        (tag as? HEAD)?.script(type, src, crossorigin, block)
        return true
    }

    override fun afterTagEnd(consumer: TagConsumer<*>, tag: Tag) {}
}