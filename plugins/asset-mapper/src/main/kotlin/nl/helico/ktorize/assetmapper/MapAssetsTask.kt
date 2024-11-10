package nl.helico.ktorize.assetmapper

import nl.helico.ktorize.assetmapper.handlers.CSSHandler
import nl.helico.ktorize.assetmapper.readers.FileAssetReader
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.isRegularFile
import kotlin.io.path.walk

abstract class MapAssetsTask @Inject constructor(
    private val sourceSetContainer: SourceSetContainer,
    private val extension: AssetMapperExtension
) : DefaultTask() {

    companion object {
        val NAME = "mapAssets"
        val DESCRIPTION = "Map assets to static assets"
    }

    init {
        group = AssetMapperPlugin.GROUP
        description = DESCRIPTION
    }

    @TaskAction
    fun run() {
        val mainSourceSet = sourceSetContainer.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        val resourcesSrcDir = mainSourceSet.resources.srcDirs.first()
        val assetsDir = project.buildFile.parentFile.resolve(extension.assetDirectory.get())
        val relative = assetsDir.relativeTo(resourcesSrcDir)
        val resourcesOutDir = mainSourceSet.output.resourcesDir
        val resourcesPath = resourcesOutDir?.resolve(relative)
        require(resourcesPath != null && resourcesPath.exists()) { "Could not find resources directory for main source set" }
        mapAssets(resourcesPath.toPath())
    }

    @OptIn(ExperimentalPathApi::class)
    private fun mapAssets(basePath: Path) {
        logger.lifecycle("Mapping all assets in $basePath")

        val assetMapper = AssetMapper(
            reader = FileAssetReader(basePath),
            handlers = listOf(
                CSSHandler()
            ),
            pathTransformer = AssetPathTransformer(),
            digester = MD5AssetDigester
        )

        val filesToDelete = mutableListOf<Path>()
        val filesToCreate = mutableListOf<Pair<Path, ByteArray>>()

        basePath.walk().forEach { path ->
            if (!path.isRegularFile()) return@forEach

            val relative = basePath.relativize(path)

            val result = assetMapper.map(relative)

            when (result) {
                is AssetMapper.MapResult.NotFound -> logger.warn("No handler found for $relative")
                is AssetMapper.MapResult.Error -> logger.error("Error mapping $relative", result.error)
                is AssetMapper.MapResult.Mapped -> {
                    logger.info("Mapped $relative to ${result.output.path}")
                    filesToDelete.add(relative)
                    filesToCreate.add(result.output.path to result.output.source)
                }
            }
        }

        filesToCreate.forEach { (path, content) ->
            basePath.resolve(path).toFile().writeBytes(content)
        }

        filesToDelete.forEach { basePath.resolve(it).toFile().delete() }
    }
}