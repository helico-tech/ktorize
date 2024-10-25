package nl.helico.ktorize.hotwire.stimulus

import io.ktor.server.application.*
import nl.helico.ktorize.html.AddScriptHook
import nl.helico.ktorize.html.hooks

internal val name = "HotwireStimulusPlugin"

class HotwireStimulusConfiguration {
    var src: String = "https://cdn.jsdelivr.net/npm/@hotwired/stimulus@3.2.2/dist/stimulus.min.js"
}

val HotwireStimulusPlugin = createApplicationPlugin(name, { HotwireStimulusConfiguration() }) {
    onCall { call ->

        val controllerRegistry = ControllerRegistry()
        call.attributes.put(ControllerRegistry.Key, controllerRegistry)

        call.hooks.add(AddScriptHook(src = this.pluginConfig.src))
        call.hooks.add(StimulusControllerAttributeHook(controllerRegistry))
    }
}