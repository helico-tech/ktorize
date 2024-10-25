package nl.helico.ktorize.hotwire.stimulus

import kotlinx.html.*
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.html.DeferredTagConsumer
import nl.helico.ktorize.html.Hook

class StimulusControllerAttributeHook(
    private val registry: ControllerRegistry,
) : Hook.TagEnd {
    override fun beforeTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag): Boolean {
        return true
    }

    override fun afterTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag) {
        if (tag.attributes.contains("data-controller")) {
            val controllerName = tag.attributes["data-controller"]!!
            registry.registerIdentifier(controllerName)
        }
    }
}

class StimulusSetupScriptsHook(
    private val registry: ControllerRegistry,
    private val assetMapper: AssetMapper,
    private val controllerResolver: ControllerResolver,
) : Hook.TagEnd {
    override fun beforeTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag): Boolean {
        return true
    }

    override fun afterTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag) {

    }
}