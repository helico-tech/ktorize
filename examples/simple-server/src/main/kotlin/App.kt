import controllers.index
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import nl.helico.ktorize.hotwire.turbo.HotwireTurboPlugin
import nl.helico.ktorize.importmap.ImportMapPlugin

fun main(args: Array<String>) {
    return EngineMain.main(args)
}

fun Application.root() {
    //install(AssetMapperPlugin)
    /*install(ImportMapPlugin)
    install(BootstrapPlugin)
    install(HotwireStimulusPlugin)
    install(HotwireTurboPlugin)*/

    routing {
        index()
    }
}