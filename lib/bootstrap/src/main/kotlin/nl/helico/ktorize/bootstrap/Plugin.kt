package nl.helico.ktorize.bootstrap

import io.ktor.server.application.*
import kotlinx.html.ScriptCrossorigin
import kotlinx.html.link
import kotlinx.html.script
import nl.helico.ktorize.html.AddHeadContentRenderPass
import nl.helico.ktorize.html.renderContext

internal val name = "Bootstrap"

val BootstrapPlugin = createRouteScopedPlugin(name) {
    onCall { call ->
        val headRenderPass = AddHeadContentRenderPass {
            link(
                rel = "stylesheet",
                href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css",
            ) {
                integrity = "sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
                attributes["crossorigin"] = "anonymous"
            }

            script(
                src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js",
                crossorigin = ScriptCrossorigin.anonymous,
            ) {
                defer = true
                integrity = "sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            }
        }

        call.renderContext.addRenderPass(headRenderPass)
    }
}