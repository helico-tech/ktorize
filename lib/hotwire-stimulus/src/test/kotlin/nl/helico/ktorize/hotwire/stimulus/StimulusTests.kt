package nl.helico.ktorize.hotwire.stimulus

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.html.*
import nl.helico.ktorize.html.respondHtml
import nl.helico.ktorize.importmap.ImportMapPlugin
import kotlin.test.Test
import kotlin.test.assertTrue

class StimulusTests {

    /*@Test
    fun helloWorld() = testApplication {

        lateinit var registry: ControllerRegistry

        application {
            //install(AssetMapperPlugin)
            install(ImportMapPlugin)
            //install(HotwireStimulusPlugin)

            routing {
                get("/") {
                    call.respondHtml {
                        registry = call.attributes[ControllerRegistry.Key]
                        head {
                            title { +"Hello, World!" }
                        }
                        body {
                            div {
                                stimulusController("hello")
                                +"Hello, World!"
                            }
                        }
                    }
                }
            }
        }

        val response = client.get("/")

        val body = response.bodyAsText()

        val expected = """<!DOCTYPE html><html>
  <head>
    <title>Hello, World!</title>
    <script type="importmap">{
    "imports": {
        "/controllers/hello": "/assets/controllers/hello-controller.692a6c14f25caa744a757225a84bc4a2.js",
        "@hotwired/stimulus": "https://cdn.jsdelivr.net/npm/@hotwired/stimulus@3.2.2/dist/stimulus.min.js"
    }
}</script>
    <script type="module">import { Application } from "@hotwired/stimulus";

import helloController from "/controllers/hello";

window.Stimulus = Application.start();

Stimulus.register("hello", helloController);</script>
  </head>
  <body>
    <div data-controller="hello">Hello, World!</div>
  </body>
</html>
"""

        assertTrue(body.contains(expected))
        assertTrue(registry.identifiers().contains("hello"))
    }*/

}