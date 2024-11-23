package nl.helico.ktorize.hotwire.stimulus

import kotlinx.html.TagConsumer
import nl.helico.ktorize.html.DeferredTagConsumer
import nl.helico.ktorize.html.RenderPass
import nl.helico.ktorize.html.TagConsumerAction

class StimulusControllerRenderPass(
    val registry: ControllerRegistry
) : RenderPass {
    override fun before(action: TagConsumerAction, consumer: DeferredTagConsumer<*>): Boolean {
        return true
    }

    override fun after(action: TagConsumerAction, consumer: TagConsumer<*>) {
        if (action !is TagConsumerAction.TagEnd) return

        if (action.tag.attributes.contains("data-controller")) {
            val controllerName = action.tag.attributes["data-controller"]!!
            registry.registerIdentifier(controllerName)
        }
    }
}

class SetupStimulusSetupScriptRenderPass()