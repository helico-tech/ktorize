package nl.helico.ktorize.hotwire.turbo

import io.ktor.server.application.*
import nl.helico.ktorize.html.AddScriptHook
import nl.helico.ktorize.html.hooks

internal val name = "HotwireTurboPlugin"

class HotwireTurboConfiguration {
    var src: String = "https://cdn.jsdelivr.net/npm/@hotwired/turbo@8.0.12/dist/turbo.es2017-umd.min.js"
}

val HotwireTurboPlugin = createApplicationPlugin(name, { HotwireTurboConfiguration() }) {

    onCall { call ->
        val hook = AddScriptHook(src = this.pluginConfig.src)
        call.hooks.add(hook)
    }
}