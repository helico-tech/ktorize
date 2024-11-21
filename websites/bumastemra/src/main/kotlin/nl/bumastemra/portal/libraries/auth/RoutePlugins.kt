package nl.bumastemra.portal.libraries.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

class AuthGuardConfiguration {
    var roles = emptyList<String>()
}

val AuthGuardPlugin = createRouteScopedPlugin(
    name = "AuthGuardPlugin",
    createConfiguration = ::AuthGuardConfiguration
) {
    val roles = pluginConfig.roles
    onCall { call ->
        val user = call.user
        if (user == null) {
            call.respond(HttpStatusCode.Forbidden)
            return@onCall
        }

        if (roles.isEmpty()) return@onCall

        if (roles.all(user.roles::contains)) {
            return@onCall
        }

        call.respond(HttpStatusCode.Forbidden)
    }
}

fun Route.authGuard(vararg roles: String, build: Route.() -> Unit) {
    val authRoute = createChild(AuthRouteSelector())
    authRoute.install(AuthGuardPlugin) {
        this.roles = roles.toList()
    }
    authRoute.build()
}

class AuthRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }
}