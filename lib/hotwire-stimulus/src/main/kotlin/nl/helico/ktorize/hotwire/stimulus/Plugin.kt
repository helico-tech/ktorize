package nl.helico.ktorize.hotwire.stimulus

import io.ktor.server.application.*
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.AssetMapperConfiguration
import nl.helico.ktorize.html.AddScriptHook
import nl.helico.ktorize.html.hooks

internal val name = "HotwireStimulusPlugin"

class HotwireStimulusConfiguration {
    var src: String = "https://cdn.jsdelivr.net/npm/@hotwired/stimulus@3.2.2/dist/stimulus.min.js"
}

val HotwireStimulusPlugin = createApplicationPlugin(name, { HotwireStimulusConfiguration() }) {

    onCall { call ->

        val assetMapper = call.application.attributes.getOrNull(AssetMapper.Key) ?: error("AssetMapper not found")

        val assetMapperConfig = call.application.attributes.getOrNull(AssetMapperConfiguration.Key) ?: error("AssetMapperConfiguration not found")

        val controllerResolver = ControllerResolver(
            basePackage = assetMapperConfig.basePackage + "/controllers",
            remotePath = assetMapperConfig.remotePath + "/controllers",
            classLoader = call.application.environment.classLoader
        )

        val controllerRegistry = ControllerRegistry()
        call.attributes.put(ControllerRegistry.Key, controllerRegistry)

        //call.hooks.add(AddScriptHook(src = this.pluginConfig.src, type = "module"))
        call.hooks.add(StimulusControllerAttributeHook(controllerRegistry))
        call.hooks.add(StimulusSetupScriptsHook(controllerRegistry, assetMapper, controllerResolver))
    }
}