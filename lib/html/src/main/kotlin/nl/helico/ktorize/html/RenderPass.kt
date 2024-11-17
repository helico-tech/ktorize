package nl.helico.ktorize.html

import io.ktor.util.*
import kotlinx.html.*

interface RenderPass {
    companion object {
        val Key = AttributeKey<RenderPass>("RenderPass")
    }

    fun before(action: TagConsumerAction, consumer: DeferredTagConsumer<*>): Boolean
    fun after(action: TagConsumerAction, consumer: TagConsumer<*>)
}

class AddScriptRenderPass(val type: String? = null,
                          val src: String? = null,
                          val crossorigin: ScriptCrossorigin? = null,
                          val block: SCRIPT.() -> Unit = {}, ) : RenderPass {
    override fun before(action: TagConsumerAction, consumer: DeferredTagConsumer<*>): Boolean {
        if (action is TagConsumerAction.TagEnd && action.tag is HEAD) {
            consumer.script(type = type, src = src, crossorigin = crossorigin, block = block)
        }
        return true
    }

    override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {}
}

class AddHeadContentRenderPass(val block: HEAD.() -> Unit) : RenderPass {
    override fun before(action: TagConsumerAction, consumer: DeferredTagConsumer<*>): Boolean {
        if (action is TagConsumerAction.TagEnd && action.tag is HEAD) {
            action.tag.block()
        }
        return true
    }

    override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {}
}

class CombinedRenderPass(val passes: List<RenderPass>) : RenderPass {
    override fun before(action: TagConsumerAction, consumer: DeferredTagConsumer<*>): Boolean {
        passes.forEach { if (!it.before(action, consumer)) return false }
        return true
    }

    override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {
        passes.forEach { it.after(action, consumer) }
    }
}