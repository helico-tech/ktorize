package nl.bumastemra.portal

import io.ktor.server.application.*
import io.ktor.server.netty.*
import nl.bumastemra.portal.features.auth.authFeature

fun Application.root() {
  authFeature()
}

fun main(args: Array<String>) {
  return EngineMain.main(args)
}