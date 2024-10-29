package nl.helico.ktorize.hotwire.turbo

import io.ktor.server.application.*
import kotlinx.html.unsafe
import nl.helico.ktorize.html.AddScriptRenderPass
import nl.helico.ktorize.html.renderContext
import nl.helico.ktorize.importmap.ImportMapBuilder

internal val name = "HotwireTurboPlugin"

class HotwireTurboConfiguration {
    var src: String = "https://cdn.jsdelivr.net/npm/@hotwired/turbo@8.0.12/+esm"
}

val HotwireTurboPlugin = createRouteScopedPlugin(name, { HotwireTurboConfiguration() }) {

    onCall { call ->

        val importMapBuilder = call.attributes.getOrNull(ImportMapBuilder.Key) ?: error("ImportMapBuilder not found")

        importMapBuilder.addModuleSpecifier("@hotwired/turbo", pluginConfig.src)

        val renderPass = AddScriptRenderPass(type = "module") {
            unsafe {
                raw(
                    """
                    import * as Turbo from "@hotwired/turbo"
                    """.trimIndent()
                )
            }
        }

        call.renderContext.addRenderPass(renderPass)
    }
}