package nl.bumastemra.portal

import io.ktor.server.application.*
import io.ktor.server.netty.*
import nl.bumastemra.portal.features.dashboard.dashboard
import nl.bumastemra.portal.libraries.auth.FusionAuthPlugin
import nl.helico.ktorize.assetmapper.AssetMapperPlugin
import nl.helico.ktorize.hotwire.turbo.HotwireTurboPlugin
import nl.helico.ktorize.importmap.ImportMapPlugin

fun Application.root() {
  install(FusionAuthPlugin)
  install(AssetMapperPlugin)
  install(ImportMapPlugin)
  //install(HotwireTurboPlugin)

  dashboard()
}

fun main(args: Array<String>) {
  return EngineMain.main(args)
}