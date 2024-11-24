package nl.bumastemra.portal

import io.ktor.server.application.*
import io.ktor.server.netty.*
import nl.bumastemra.portal.features.userprofiles.UserProfileModule
import nl.bumastemra.portal.libraries.auth.OAuthPlugin
import nl.bumastemra.portal.routes.routes
import nl.helico.ktorize.assetmapper.AssetMapperPlugin
import nl.helico.ktorize.di.DIPlugin
import nl.helico.ktorize.importmap.ImportMapPlugin

fun Application.root() {
  install(OAuthPlugin)
  install(AssetMapperPlugin)
  install(ImportMapPlugin)

  install(DIPlugin {
    import(UserProfileModule)
  })

  routes()
}

fun main(args: Array<String>) {
  return EngineMain.main(args)
}