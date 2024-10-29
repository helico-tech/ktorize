package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.logging.*
import io.ktor.util.pipeline.*
import nl.helico.ktorize.html.renderContext

internal val name = "AssetMapper"

internal val LOGGER = KtorSimpleLogger(name)

class AssetMapperConfiguration {
    var basePackage: String = "assets"
    var remotePath: String = "/assets"
    var tagNames: MutableList<String> = mutableListOf("img", "script", "link", "a")
    var attributeNames: MutableList<String> = mutableListOf("src", "href")
    var cacheDuration: Int = 31536000

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

    LOGGER.info("Configured AssetMapper: $assetMapper")
    LOGGER.info("Base Package: ${pluginConfig.basePackage}")
    LOGGER.info("Remote Path: ${pluginConfig.remotePath}")
    LOGGER.info("Tag Names: ${pluginConfig.tagNames}")
    LOGGER.info("Attribute Names: ${pluginConfig.attributeNames}")

    application.attributes.put(AssetMapper.Key, assetMapper)

    LOGGER.info("Registered AssetMapper under Application Attributes with key ${AssetMapper.Key}")

    application.attributes.put(AssetMapperConfiguration.Key, pluginConfig)

    LOGGER.info("Registered AssetMapperConfiguration under Application Attributes with key ${AssetMapperConfiguration.Key}")

    val transformers = listOf(
        CSSAssetTransformer(assetMapper)
    )

    LOGGER.info("Registered Asset Transformers: $transformers")

    application.routing {

        get(assetMapper.pathRegex) {
            LOGGER.debug("Asset requested: ${call.request.uri}")
            val directoryName = call.parameters["directoryName"]!!.removePrefix("/")
            val baseName = call.parameters["baseName"]!!
            val extension = call.parameters["extension"]!!

            LOGGER.debug("Asset parts: $directoryName, $baseName, $extension")

            val actualPath = listOf(
                pluginConfig.remotePath,
                directoryName,
                "$baseName.$extension"
            ).filterNot { it.isEmpty() }.joinToString("/")

            LOGGER.debug("Redirecting to: $actualPath")

            call.pipelineCall.redirectInternally(actualPath)
        }

        staticResources(pluginConfig.remotePath, pluginConfig.basePackage) {
            cacheControl { listOf(Immutable, CacheControl.MaxAge(pluginConfig.cacheDuration)) }
        }
    }

    // add the rendering hook
    onCall { call ->
        val pass = AssetMapperRenderPass(assetMapper, pluginConfig.tagNames, pluginConfig.attributeNames)
        call.renderContext.addRenderPass(pass)
    }

    onCallRespond { call ->
        transformBody { data ->
            val transformer = transformers.firstOrNull { it.accepts(call, data) }
            if (transformer != null) {
                LOGGER.debug("Transforming asset {} with transformer {}", call.request.uri, transformer)
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