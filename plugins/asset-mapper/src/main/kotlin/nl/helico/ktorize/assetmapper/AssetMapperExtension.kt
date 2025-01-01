package nl.helico.ktorize.assetmapper

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import java.nio.file.Path
import javax.inject.Inject

abstract class AssetMapperExtension @Inject constructor(
    private val sourceSetContainer: SourceSetContainer,
    private val project: Project
) {
    companion object {
        val NAME = "assetMapper"
    }

    abstract val assetsBasePackage: Property<File>

    init {
        assetsBasePackage.convention(File("assets"))
    }

    fun getResourcesPath(): Path {
        val mainSourceSet = sourceSetContainer.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        val resourcesDir = mainSourceSet.resources.srcDirs.firstOrNull() ?: error("No resources directory found for main source set")
        val resourcesPath = resourcesDir.resolve(assetsBasePackage.get())

        if (!resourcesPath.exists()) {
            resourcesPath.mkdirs()
        }

        return resourcesPath.toPath()
    }

    fun getGeneratedDirectory(): File {
        return project.layout.buildDirectory.get().asFile.resolve("generated/ktorize")
    }

}