package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.logging.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import nl.helico.ktorize.html.renderingPipeline

internal val name = "AssetMapper"

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

    companion object {
        val Key = AttributeKey<AssetMapperConfiguration>("AssetMapperConfiguration")
    }
}

val AssetMapperPlugin = createApplicationPlugin(name, { AssetMapperConfiguration() }) {

    val assetMapper = pluginConfig.factory(this)

    val transformers = listOf(
        CSSAssetTransformer(assetMapper)
    )

    application.routing {
        application.attributes.put(AssetMapper.Key, assetMapper)
        application.attributes.put(AssetMapperConfiguration.Key, pluginConfig)

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
        call.renderingPipeline.addHook(AssetMapperHook(assetMapper, pluginConfig.tagNames, pluginConfig.attributeNames))
    }

    onCallRespond { call ->
        transformBody { data ->
            val transformer = transformers.firstOrNull { it.accepts(call, data) }
            if (transformer != null) {
                val transformed = transformer.transform(call, data)
                transformed
            } else {
                data
            }
        }
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