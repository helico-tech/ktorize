package nl.helico.ktorize.hotwire.stimulus

import io.ktor.server.application.*
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.AssetMapperConfiguration
import nl.helico.ktorize.html.renderingPipeline
import nl.helico.ktorize.importmap.ImportMapBuilder

internal val name = "HotwireStimulusPlugin"

class HotwireStimulusConfiguration {
    val controllerPrefix = "/controllers"
    var src: String = "https://cdn.jsdelivr.net/npm/@hotwired/stimulus@3.2.2/dist/stimulus.min.js"
}

val HotwireStimulusPlugin = createRouteScopedPlugin(name, { HotwireStimulusConfiguration() }) {

    onCall { call ->
        val assetMapper = call.application.attributes.getOrNull(AssetMapper.Key) ?: error("AssetMapper not found")
        val assetMapperConfig = call.application.attributes.getOrNull(AssetMapperConfiguration.Key) ?: error("AssetMapperConfiguration not found")
        val importMapBuilder = call.attributes.getOrNull(ImportMapBuilder.Key) ?: error("ImportMapBuilder not found")

        val controllerRegistry = ControllerRegistry()
        call.attributes.put(ControllerRegistry.Key, controllerRegistry)

        val controllerResolver = ControllerResolver(
            basePackage = assetMapperConfig.basePackage + pluginConfig.controllerPrefix,
            remotePath = assetMapperConfig.remotePath + pluginConfig.controllerPrefix,
            classLoader = call.application.environment.classLoader
        )

        importMapBuilder.addModuleSpecifier("@hotwired/stimulus", pluginConfig.src)
        importMapBuilder.addProvider { builder ->
            controllerRegistry.identifiers()
                .map { id -> "${pluginConfig.controllerPrefix}/${id}" to controllerResolver.resolveController(id) }
                .map { (id, src) -> id to assetMapper.map(src) }
                .forEach { (id, src) -> builder.addModuleSpecifier(id, src) }
        }

        call.renderingPipeline.addHook(StimulusControllerAttributeHook(controllerRegistry))
        call.renderingPipeline.addHook(StimulusSetupScriptsHook(StimulusInitScript(pluginConfig.controllerPrefix, controllerRegistry)))
    }
}