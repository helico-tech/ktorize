package nl.bumastemra.portal

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import nl.bumastemra.portal.features.dashboard.dashboard
import nl.helico.ktorize.hotwire.turbo.HotwireTurboPlugin
import nl.helico.ktorize.importmap.ImportMapPlugin

internal object Immutable : CacheControl(null) {
  override fun toString(): String = "immutable"
}

fun Application.root() {
  install(ImportMapPlugin)
  install(HotwireTurboPlugin)

  routing {
    staticResources("/assets", "assets") {
      cacheControl {
        listOf(CacheControl.MaxAge(365 * 24 * 3600), Immutable)
      }
    }
  }

  dashboard()
}

fun main(args: Array<String>) {
  return EngineMain.main(args)
}