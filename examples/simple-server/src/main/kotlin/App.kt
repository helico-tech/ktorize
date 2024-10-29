import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.html.*
import nl.helico.ktorize.assetmapper.AssetMapperPlugin
import nl.helico.ktorize.di.DI
import nl.helico.ktorize.di.DIModuleRegistry
import nl.helico.ktorize.di.DIPlugin
import nl.helico.ktorize.hotwire.stimulus.HotwireStimulusPlugin
import nl.helico.ktorize.hotwire.stimulus.stimulusController
import nl.helico.ktorize.hotwire.turbo.HotwireTurboPlugin
import nl.helico.ktorize.html.respondHtml
import nl.helico.ktorize.importmap.ImportMapPlugin
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance

fun main() {
    embeddedServer(Netty, port = 8080) {

        DIModuleRegistry.register("Test") {
            bindSingleton<String>("test") { "Hello, World!" }
        }

        install(AssetMapperPlugin)
        install(ImportMapPlugin)
        install(HotwireTurboPlugin)
        install(HotwireStimulusPlugin)
        install(DIPlugin)

        routing {

            val greeting = application.DI.direct.instance<String>("test")

            get("/") {
                call.respondHtml {
                    head {
                        title { + greeting}
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