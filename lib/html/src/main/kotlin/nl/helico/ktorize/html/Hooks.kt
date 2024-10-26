package nl.helico.ktorize.html

import kotlinx.html.*

interface Hook {
    interface Comment : Hook {
        fun beforeComment(consumer: DeferredTagConsumer<*>, content: CharSequence): Boolean
        fun afterComment(consumer: DeferredTagConsumer<*>, content: CharSequence)
    }

    interface Content : Hook {
        fun beforeContent(consumer: DeferredTagConsumer<*>, content: CharSequence): Boolean
        fun afterContent(consumer: DeferredTagConsumer<*>, content: CharSequence)
    }

    interface ContentEntity : Hook {
        fun beforeContentEntity(consumer: DeferredTagConsumer<*>, entity: Entities): Boolean
        fun afterContentEntity(consumer: DeferredTagConsumer<*>, entity: Entities)
    }

    interface ContentUnsafe : Hook {
        fun beforeContentUnsafe(consumer: DeferredTagConsumer<*>, block: Unsafe.() -> Unit): Boolean
        fun afterContentUnsafe(consumer: DeferredTagConsumer<*>, block: Unsafe.() -> Unit)
    }

    interface TagStart : Hook {
        fun beforeTagStart(consumer: DeferredTagConsumer<*>, tag: Tag): Boolean
        fun afterTagStart(consumer: DeferredTagConsumer<*>, tag: Tag)
    }

    interface TagEnd : Hook {
        fun beforeTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag): Boolean
        fun afterTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag)
    }

    interface Finalize : Hook {
        fun beforeFinalize(consumer: DeferredTagConsumer<*>)
    }
}

class AddScriptHook(
    val type: String? = null,
    val src: String? = null,
    val crossorigin: ScriptCrossorigin? = null,
    val block: SCRIPT.() -> Unit = {}
) : Hook.TagEnd {
    override fun beforeTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag): Boolean {
        (tag as? HEAD)?.script(type, src, crossorigin, block)
        return true
    }

    override fun afterTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag) {}
}