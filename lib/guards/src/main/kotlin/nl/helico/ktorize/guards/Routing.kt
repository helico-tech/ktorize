package nl.helico.ktorize.guards

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

typealias OnUnauthorized = suspend (call: ApplicationCall, result: Guard.AuthorizationResult.Unauthorized) -> Unit

class GuardPluginConfiguration {
    var guards = emptyList<Guard>()
    var onUnauthorized: OnUnauthorized = { call, result ->
        call.respondText(status = result.statusCode, text = result.message)
    }
}

class GuardRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }
}

val GuardPlugin = createRouteScopedPlugin(
    name = "GuardPlugin",
    createConfiguration = ::GuardPluginConfiguration
) {
    val guards = pluginConfig.guards

    onCall { call ->
        for (guard in guards) {
            when (val result = guard.authorize(call)) {
                is Guard.AuthorizationResult.Success -> continue
                is Guard.AuthorizationResult.Unauthorized -> {
                    if (guard.onUnauthorized != null) {
                        guard.onUnauthorized!!(call, result)
                        return@onCall
                    }

                    pluginConfig.onUnauthorized(call, result)
                    return@onCall
                }
            }
        }
    }
}

fun Route.guard(vararg guards: Guard, build: Route.() -> Unit) {
    guard(guards = guards, onUnauthorized = null, build = build)
}

fun Route.guard(vararg guards: Guard, onUnauthorized: OnUnauthorized?, build: Route.() -> Unit) {
    val guardRoute = createChild(GuardRouteSelector())
    guardRoute.install(GuardPlugin) {
        this.guards = guards.toList()
        if (onUnauthorized != null) {
            this.onUnauthorized = onUnauthorized
        }
    }
    guardRoute.build()
}