package nl.helico.ktorize.html

import io.ktor.util.*
import kotlinx.html.TagConsumer

interface RenderPass {
    companion object {
        val Key = AttributeKey<RenderPass>("RenderPass")
    }

    fun before(action: TagConsumerAction, consumer: TagConsumer<*>): Boolean
    fun after(action: TagConsumerAction, consumer: TagConsumer<*>)
}

fun MultiPassConsumer.actions(): List<TagConsumerAction>  {

    val actions = mutableListOf<TagConsumerAction>()

    val pass = object : RenderPass {
        override fun before(action: TagConsumerAction, consumer: TagConsumer<*>): Boolean {
            actions.add(action)
            return true
        }

        override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {}
    }

    this.applyRenderPass(pass)

    return actions
}