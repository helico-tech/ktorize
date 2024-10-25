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
        if (tag !is HEAD) return true

        consumer.defer { downstream ->
            val controllerNames = registry.identifiers()

            if (controllerNames.isEmpty()) return@defer

            val sources = controllerNames
                .map { controllerResolver.resolveController(it) }
                .map { assetMapper.map(it) }

            SCRIPT(
                initialAttributes = mapOf("type" to "importmap"),
                consumer = downstream
            ).visit {
                unsafe {
                    raw("""
                        {
                            "imports": {
                                "@hotwired/stimulus": "https://cdn.jsdelivr.net/npm/@hotwired/stimulus@3.2.2/dist/stimulus.min.js"
                            }
                        }
                    """.trimIndent())
                }
            }

            sources.forEach { source ->
                SCRIPT(
                    initialAttributes = mapOf("src" to source, "type" to "module"),
                    consumer = downstream
                ).visit {}
            }
        }
        return true
    }

    override fun afterTagEnd(consumer: DeferredTagConsumer<*>, tag: Tag) {

    }
}