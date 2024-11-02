package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.logging.*
import io.ktor.util.pipeline.*
import nl.helico.ktorize.assetmapper.handlers.CSSHandler
import nl.helico.ktorize.assetmapper.readers.ResourceAssetReader
import nl.helico.ktorize.assetmapper.readers.WebAssetReader
import kotlin.io.path.Path

internal val name = "AssetMapper"

internal val LOGGER = KtorSimpleLogger(name)

class AssetMapperConfiguration {
    var basePackage: String = "assets"
    var basePath: String = "/assets"
    var tagNames: MutableList<String> = mutableListOf("img", "script", "link", "a")
    var attributeNames: MutableList<String> = mutableListOf("src", "href")
    var cacheDuration: Int = 31536000

    var factory: PluginBuilder<AssetMapperConfiguration>.() -> AssetMapper = { ->

        val reader = WebAssetReader(
            basePath = Path(basePath),
            downstream = ResourceAssetReader(
                basePackage = Path(basePackage).normalize(),
                classLoader = environment.classLoader
            )
        )

        val handlers = listOf(CSSHandler())

        AssetMapper(reader, handlers)
    }

    companion object {
        val Key = AttributeKey<AssetMapperConfiguration>("AssetMapperConfiguration")
    }
}

val AssetMapperPlugin = createApplicationPlugin(name, { AssetMapperConfiguration() }) {

    val assetMapper = pluginConfig.factory(this)

    application.attributes.put(AssetMapper.Key, assetMapper)
    application.attributes.put(AssetMapperConfiguration.Key, pluginConfig)

    application.routing {
        get(assetMapper.getMappedAssetRegex(Path(pluginConfig.basePath))) {
            val directoryName = call.parameters["directoryName"]!!.removePrefix("/")
            val baseName = call.parameters["baseName"]!!
            val extension = call.parameters["extension"]!!

            val actualPath = Path(
                pluginConfig.basePath,
                directoryName,
                "$baseName.$extension"
            ).normalize()

            val asset = assetMapper.map(actualPath)

            when (asset) {
                is AssetMapper.MapResult.NotFound -> call.respond(HttpStatusCode.NotFound)
                is AssetMapper.MapResult.Error -> call.respond(HttpStatusCode.InternalServerError, asset.error.message ?: "")
                is AssetMapper.MapResult.Mapped -> {
                    val output = asset.output
                }
            }

            call.respondText { actualPath .toString()}
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