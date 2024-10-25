package nl.helico.ktorize.hotwire.stimulus

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import nl.helico.ktorize.html.Hook

class StimulusControllerAttributeHook(
    private val registry: ControllerRegistry,
) : Hook.TagEnd {
    override fun beforeTagEnd(consumer: TagConsumer<*>, tag: Tag): Boolean {
        return true
    }

    override fun afterTagEnd(consumer: TagConsumer<*>, tag: Tag) {
        if (tag.attributes.contains("data-controller")) {
            val controllerName = tag.attributes["data-controller"]!!
            registry.registerName(controllerName)
        }
    }
}