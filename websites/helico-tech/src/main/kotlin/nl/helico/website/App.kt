package nl.helico.website

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import nl.helico.ktorize.assetmapper.AssetMapperPlugin
import nl.helico.ktorize.hotwire.turbo.HotwireTurboPlugin
import nl.helico.ktorize.importmap.ImportMapPlugin

fun main(args: Array<String>) {
    return EngineMain.main(args)
}

fun Application.root() {
    install(AssetMapperPlugin) {
        cssAutoLoadPrefix = "ht-"
    }
    install(ImportMapPlugin)
    install(HotwireTurboPlugin)

    routes()
}