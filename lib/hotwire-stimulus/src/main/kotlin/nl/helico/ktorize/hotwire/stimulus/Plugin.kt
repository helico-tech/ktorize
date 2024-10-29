package nl.helico.ktorize.hotwire.stimulus

import io.ktor.server.application.*
import io.ktor.util.logging.*
import kotlinx.html.unsafe
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.AssetMapperConfiguration
import nl.helico.ktorize.html.AddScriptRenderPass
import nl.helico.ktorize.html.renderContext
import nl.helico.ktorize.importmap.ImportMapBuilder

internal val name = "HotwireStimulusPlugin"

internal val LOGGER = KtorSimpleLogger(name)

class HotwireStimulusConfiguration {
    val controllerPrefix = "/controllers"
    var src: String = "https://cdn.jsdelivr.net/npm/@hotwired/stimulus@3.2.2/dist/stimulus.min.js"
}

val HotwireStimulusPlugin = createRouteScopedPlugin(name, { HotwireStimulusConfiguration() }) {

    val assetMapper = application.attributes.getOrNull(AssetMapper.Key) ?: error("AssetMapper not found")
    val assetMapperConfig = application.attributes.getOrNull(AssetMapperConfiguration.Key) ?: error("AssetMapperConfiguration not found")

    onCall { call ->

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

        val initScript = StimulusInitScript(pluginConfig.controllerPrefix, controllerRegistry)

        val setupScriptPass = AddScriptRenderPass(type = "module") {
            unsafe {
                +initScript.script()
            }
        }

        call.renderContext.addRenderPass(StimulusControllerRenderPass(controllerRegistry))
        call.renderContext.addRenderPass(setupScriptPass)
    }
}