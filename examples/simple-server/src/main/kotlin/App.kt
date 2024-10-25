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
import nl.helico.ktorize.hotwire.turbo.HotwireTurboPlugin
import nl.helico.ktorize.hotwire.turbo.TurboFrame
import nl.helico.ktorize.html.respondHtml

fun main() {
    embeddedServer(Netty, port = 8080) {

        install(HotwireTurboPlugin)
        install(AssetMapperPlugin)

        routing {
            get("/") {
                call.respondHtml {
                    html {
                        head {                        }
                        body {
                            TurboFrame("foo") {
                                h1 { + "Hello world" }
                            }

                            img(src = "/assets/deeper/donkey-2.jpg") {
                                alt = "An image"
                            }
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}