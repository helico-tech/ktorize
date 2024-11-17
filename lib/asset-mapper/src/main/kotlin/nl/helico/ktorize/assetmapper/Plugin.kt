package nl.helico.ktorize.assetmapper

import io.ktor.http.CacheControl
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing
import nl.helico.ktorize.html.renderContext
import kotlin.io.path.Path

internal object Immutable : CacheControl(null) {
    override fun toString(): String = "immutable"
}

internal val name = "AssetMapperPlugin"

val AssetMapperPlugin = createApplicationPlugin(name) {

    // load as string
    val mappedConfigurationJson = environment.classLoader.getResourceAsStream("ktorize/${MappedAssetsConfiguration.DEFAULT_FILE_NAME}")!!.bufferedReader().readText()
    val mappedConfiguration = MappedAssetsConfiguration.fromJSON(mappedConfigurationJson)

    application.routing {
        staticResources(
            remotePath = Path("/").resolve(mappedConfiguration.root.path).normalize().toString(),
            mappedConfiguration.basePackage
        ) {
            cacheControl {
                listOf(CacheControl.MaxAge(365 * 24 * 3600), Immutable)
            }
        }
    }

    val renderPass = AssetMapperRenderPass(mappedConfiguration)

    onCall { call ->
        call.renderContext.addRenderPass(renderPass)
        call.renderContext.addRenderPass(CssLoaderRenderPass(mappedAssetsConfiguration = mappedConfiguration))
    }
}