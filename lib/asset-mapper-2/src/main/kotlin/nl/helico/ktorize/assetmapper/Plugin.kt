package nl.helico.ktorize.assetmapper

import io.ktor.server.application.*
import io.ktor.util.*
import io.ktor.util.logging.*
import nl.helico.ktorize.assetmapper.handlers.CSSHandler
import nl.helico.ktorize.assetmapper.readers.ResourceAssetReader
import kotlin.io.path.Path

internal val name = "AssetMapper"

internal val LOGGER = KtorSimpleLogger(name)

class AssetMapperConfiguration {
    var basePackage: String = "assets"
    var remotePath: String = "/assets"
    var tagNames: MutableList<String> = mutableListOf("img", "script", "link", "a")
    var attributeNames: MutableList<String> = mutableListOf("src", "href")
    var cacheDuration: Int = 31536000

    var factory: PluginBuilder<AssetMapperConfiguration>.() -> AssetMapper = { ->
        val reader = ResourceAssetReader(
            basePackage = Path(basePackage).normalize(),
            classLoader = environment.classLoader
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

    LOGGER.info("Configured AssetMapper: $assetMapper")
    LOGGER.info("Base Package: ${pluginConfig.basePackage}")
    LOGGER.info("Remote Path: ${pluginConfig.remotePath}")
    LOGGER.info("Tag Names: ${pluginConfig.tagNames}")
    LOGGER.info("Attribute Names: ${pluginConfig.attributeNames}")

    application.attributes.put(AssetMapper.Key, assetMapper)

    LOGGER.info("Registered AssetMapper under Application Attributes with key ${AssetMapper.Key}")

    application.attributes.put(AssetMapperConfiguration.Key, pluginConfig)

    LOGGER.info("Registered AssetMapperConfiguration under Application Attributes with key ${AssetMapperConfiguration.Key}")
}