package nl.helico.ktorize.importmap

import kotlinx.html.HEAD
import kotlinx.html.TagConsumer
import kotlinx.html.script
import kotlinx.html.unsafe
import nl.helico.ktorize.html.RenderPass
import nl.helico.ktorize.html.TagConsumerAction

class AddImportMapRenderPass(
    val builder: ImportMapBuilder
) : RenderPass {
    override fun before(action: TagConsumerAction, consumer: TagConsumer<*>): Boolean {
        if (action !is TagConsumerAction.TagEnd) return true

        if (action.tag !is HEAD) return true

        consumer.script(type = "importmap") {
            unsafe { + builder.build().toString() }
        }

        return true
    }

    override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {
    }
}