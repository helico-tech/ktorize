package nl.helico.ktorize.html

import kotlinx.html.TagConsumer

class DownstreamRenderPass(
    val downstream: TagConsumer<*>
) : RenderPass {

    override fun before(action: TagConsumerAction, consumer: TagConsumer<*>): Boolean {
        return true
    }

    override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {
        action.apply(downstream)
    }
}