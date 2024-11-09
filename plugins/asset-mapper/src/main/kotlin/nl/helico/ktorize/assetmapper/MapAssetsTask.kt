package nl.helico.ktorize.assetmapper

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

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

        logger.lifecycle("Mapping all assets in $resourcesPath")
    }
}