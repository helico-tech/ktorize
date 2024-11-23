package nl.bumastemra.portal

import io.ktor.server.application.*
import io.ktor.server.netty.*
import nl.bumastemra.portal.features.dashboard.dashboard
import nl.bumastemra.portal.libraries.auth.OAuthPlugin
import nl.helico.ktorize.assetmapper.AssetMapperPlugin
import nl.helico.ktorize.importmap.ImportMapPlugin

fun Application.root() {
  install(OAuthPlugin)
  install(AssetMapperPlugin)
  install(ImportMapPlugin)
  //install(HotwireTurboPlugin)

  dashboard()
}

fun main(args: Array<String>) {
  return EngineMain.main(args)
}