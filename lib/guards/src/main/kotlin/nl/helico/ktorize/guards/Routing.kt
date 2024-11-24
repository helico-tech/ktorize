package nl.helico.ktorize.guards

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.isHandled
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

typealias OnUnauthorized = suspend (call: ApplicationCall, result: Guard.AuthorizationResult.Unauthorized) -> Unit

class RouteGuardPluginConfiguration {
    var onUnauthorized: OnUnauthorized = { call, result ->
        call.respondText(status = result.statusCode, text = result.message)
    }
}

class RouteGuardRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }
}

fun RouteGuardPlugin(guard: Guard) = createRouteScopedPlugin(
    name = "GuardPlugin",
    createConfiguration = ::RouteGuardPluginConfiguration
) {
    onCall { call ->
        when (val result = guard.isAuthorized(call)) {
            is Guard.AuthorizationResult.Success -> return@onCall
            is Guard.AuthorizationResult.Unauthorized -> {
                (guard.onUnauthorized ?: pluginConfig.onUnauthorized).invoke(call, result)
            }
        }
    }
}

fun Route.guard(guard: Guard, build: Route.() -> Unit) {
    guard(guard = guard, onUnauthorized = null, build = build)
}

fun Route.guard(vararg guards: Guard, build: Route.() -> Unit) {
    guard(guard = allOf(*guards), onUnauthorized = null, build = build)
}

fun Route.guard(vararg guards: Guard, onUnauthorized: OnUnauthorized?, build: Route.() -> Unit) {
    guard(guard = allOf(*guards), onUnauthorized = onUnauthorized, build = build)
}

fun Route.guard(guard: Guard, onUnauthorized: OnUnauthorized?, build: Route.() -> Unit) {
    val guardRoute = createChild(RouteGuardRouteSelector())
    guardRoute.install(RouteGuardPlugin(guard)) {
        if (onUnauthorized != null) {
            this.onUnauthorized = onUnauthorized
        }
    }
    guardRoute.build()
}