package nl.bumastemra.portal

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import nl.bumastemra.portal.features.dashboard.dashboard
import nl.helico.ktorize.hotwire.turbo.HotwireTurboPlugin
import nl.helico.ktorize.importmap.ImportMapPlugin

fun Application.root() {
  install(ImportMapPlugin)
  install(HotwireTurboPlugin)

  routing {
    staticResources("/assets", "assets")
  }

  dashboard()
}

fun main(args: Array<String>) {
  return EngineMain.main(args)
}