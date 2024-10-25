package nl.helico.ktorize.hotwire.stimulus

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.html.*
import nl.helico.ktorize.assetmapper.AssetMapperPlugin
import nl.helico.ktorize.html.respondHtml
import kotlin.test.Test

class StimulusTests {

    @Test
    fun helloWorld() = testApplication {

        lateinit var stimulusSrc: String
        lateinit var registry: ControllerRegistry

        application {
            install(AssetMapperPlugin)

            install(HotwireStimulusPlugin) {
                stimulusSrc = this.src
            }

            routing {
                get("/") {
                    call.respondHtml {
                        registry = call.attributes[ControllerRegistry.Key]

                        html {
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
        }

        val response = client.get("/")

        val body = response.bodyAsText()

        assert(body.contains(stimulusSrc))
        assert(registry.identifiers().contains("hello"))

    }

}