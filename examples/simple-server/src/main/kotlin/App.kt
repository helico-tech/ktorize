import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import nl.helico.ktorize.assetmapper.AssetMapperPlugin
import nl.helico.ktorize.assetmapper.createMappedAssetRegex
import nl.helico.ktorize.hotwire.stimulus.HotwireStimulusPlugin
import nl.helico.ktorize.hotwire.stimulus.stimulusController
import nl.helico.ktorize.hotwire.turbo.HotwireTurboPlugin
import nl.helico.ktorize.hotwire.turbo.TurboFrame
import nl.helico.ktorize.html.respondHtml
import nl.helico.ktorize.importmap.ImportMapPlugin

fun main() {
    embeddedServer(Netty, port = 8080) {

        install(AssetMapperPlugin)
        install(ImportMapPlugin)
        install(HotwireTurboPlugin)
        install(HotwireStimulusPlugin)

        routing {

            get("/") {
                call.respondHtml {
                    head {
                        title { +"Hello, World!" }
                        link(rel = "stylesheet", href = "/assets/css/styles.css")
                    }
                    body {
                        div {
                            stimulusController("hello")
                            +"Hello, World!"
                        }

                        img(src = "/assets/deeper/donkey-2.jpg") {
                            alt = "An image"
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}