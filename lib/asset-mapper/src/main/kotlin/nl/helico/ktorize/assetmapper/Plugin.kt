package nl.helico.ktorize.assetmapper

import io.ktor.http.CacheControl
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing
import nl.helico.ktorize.html.renderContext

internal object Immutable : CacheControl(null) {
    override fun toString(): String = "immutable"
}

internal val name = "AssetMapperPlugin"

class AssetMapperPluginConfiguration(
    var cssAutoLoadPrefix: String = "kt-"
)

val AssetMapperPlugin = createApplicationPlugin(name, createConfiguration = ::AssetMapperPluginConfiguration) {

    // load as string
    val mappedConfigurationJson = environment.classLoader.getResourceAsStream("ktorize/${MappedAssetsConfiguration.DEFAULT_FILE_NAME}")!!.bufferedReader().readText()
    val mappedConfiguration = MappedAssetsConfiguration.fromJSON(mappedConfigurationJson)

    application.routing {
        staticResources(
            remotePath = mappedConfiguration.root.path,
            basePackage = mappedConfiguration.basePackage
        ) {
            cacheControl {
                listOf(CacheControl.MaxAge(365 * 24 * 3600), Immutable)
            }
        }
    }

    val renderPass = AssetMapperRenderPass(mappedConfiguration)

    onCall { call ->
        call.renderContext.addRenderPass(renderPass)
        call.renderContext.addRenderPass(
            CssLoaderRenderPass(
                mappedAssetsConfiguration = mappedConfiguration,
                prefix = pluginConfig.cssAutoLoadPrefix
            )
        )
    }
}