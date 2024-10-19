package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import io.ktor.util.pipeline.*
import nl.helico.ktorize.html.hooks

internal val name = "AssetMapper"

internal val LOGGER = KtorSimpleLogger(name)

class AssetMapperConfiguration {
    var basePackage: String = "assets"
    var remotePath: String = "/assets"
    var tagNames: MutableList<String> = mutableListOf("img", "script", "link", "a")
    var attributeNames: MutableList<String> = mutableListOf("src", "href")

    var factory: PluginBuilder<AssetMapperConfiguration>.() -> AssetMapper = { ->
        DefaultAssetMapper(
            baseUrl = remotePath,
            assetResolver = ResourceAssetResolver(
                basePackage = basePackage,
                classLoader = environment.classLoader
            )
        )
    }
}

val AssetMapperPlugin = createApplicationPlugin(name, { AssetMapperConfiguration() }) {

    val assetMapper = pluginConfig.factory(this).also { assetMapper ->
        if (assetMapper is AssetMapper.Preload) {
            assetMapper.preload().forEach { asset ->
                LOGGER.info("Mapped asset: $asset -> ${assetMapper.map(asset)}")
            }
        }
    }

    val hook = AssetMapperHook(assetMapper, pluginConfig.tagNames, pluginConfig.attributeNames)

    application.routing {
        get(assetMapper.pathRegex) {
            val directoryName = call.parameters["directoryName"]!!
            val baseName = call.parameters["baseName"]!!
            val extension = call.parameters["extension"]!!
            val actualPath = listOf(
                pluginConfig.remotePath,
                directoryName,
                "$baseName.$extension"
            ).filterNot { it.isEmpty() }.joinToString("/")

            call.pipelineCall.redirectInternally(actualPath)
        }

        staticResources(pluginConfig.remotePath, pluginConfig.basePackage) {
            cacheControl { listOf(Immutable, CacheControl.MaxAge(31536000)) }
        }
    }

    // add the rendering hook
    onCall { call ->
        call.hooks.add(hook)
    }
}

internal object Immutable : CacheControl(null) {
    override fun toString(): String = "immutable"
}

suspend fun PipelineCall.redirectInternally(path: String) {
    val cp = object: RequestConnectionPoint by this.request.local {
        override val uri: String = path
    }
    val req = object: PipelineRequest by this.request {
        override val local: RequestConnectionPoint = cp
    }

    val call = object: PipelineCall by this {
        override val request: PipelineRequest = req
    }

    this.application.execute(call)
}