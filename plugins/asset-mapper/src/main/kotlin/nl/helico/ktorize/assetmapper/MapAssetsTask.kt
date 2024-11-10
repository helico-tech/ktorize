package nl.helico.ktorize.assetmapper

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Provider
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.isRegularFile
import kotlin.io.path.walk

abstract class MapAssetsTask @Inject constructor(
    private val sourceSetContainer: SourceSetContainer,
    private val extension: AssetMapperExtension,
    private val assetMapperProvider: Provider<AssetMapper>
) : DefaultTask() {

    companion object {
        val NAME = "mapAssets"
        val DESCRIPTION = "Map assets to static assets"
    }

    init {
        group = AssetMapperPlugin.GROUP
        description = DESCRIPTION
    }

    private fun getGeneratedResourcesDirectory() = extension.getGeneratedDirectory().resolve("resources")

    private fun getGeneratedDirectory() = getGeneratedResourcesDirectory()
        .resolve(extension.assetsBasePackage.get())

    @OptIn(ExperimentalPathApi::class)
    @TaskAction
    fun run() {
        val basePath = extension.getResourcesPath()

        logger.lifecycle("Mapping all assets in $basePath")

        val assetMapper = this.assetMapperProvider.get()

        val filesToCreate = mutableListOf<Pair<Path, ByteArray>>()
        val filesToIgnore = mutableListOf<Path>()

        basePath.walk().forEach { path ->
            if (!path.isRegularFile()) return@forEach

            val relative = basePath.relativize(path)

            val result = assetMapper.map(relative)

            when (result) {
                is AssetMapper.MapResult.NotFound -> logger.warn("No handler found for $relative")
                is AssetMapper.MapResult.Error -> logger.error("Error mapping $relative", result.error)
                is AssetMapper.MapResult.Mapped -> {
                    logger.info("Mapped $relative to ${result.output.path}")
                    filesToCreate.add(result.output.path to result.output.source)
                    filesToIgnore.add(path)
                }
            }
        }

        filesToCreate.forEach { (path, content) ->
            val outputFile = getGeneratedDirectory().resolve(path.toFile())
            outputFile.parentFile.mkdirs()
            outputFile.writeBytes(content)
        }

        filesToIgnore.forEach { path ->
            val toIgnore = extension.assetsBasePackage.get().resolve(basePath.relativize(path).toFile())
            sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME).resources.exclude(toIgnore.toString())
        }

        registerResourcesDir()
    }

    private fun registerResourcesDir() {
        val main = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME)
        val resources = main.resources
        resources.srcDir(getGeneratedResourcesDirectory())
    }
}