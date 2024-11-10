package nl.helico.ktorize.assetmapper

import org.gradle.api.Project
import org.gradle.api.provider.Property
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

    @get:InputDirectory
    abstract val assetDirectory: Property<File>

    init {
        assetDirectory.convention(File("src/main/resources/assets"))
    }

    fun getResoucesPath(): Path {
        val mainSourceSet = sourceSetContainer.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        val resourcesSrcDir = mainSourceSet.resources.srcDirs.first()
        val assetsDir = project.buildFile.parentFile.resolve(assetDirectory.get())
        val relative = assetsDir.relativeTo(resourcesSrcDir)
        val resourcesOutDir = mainSourceSet.output.resourcesDir
        val resourcesPath = resourcesOutDir?.resolve(relative)
        require(resourcesPath != null && resourcesPath.exists()) { "Could not find resources directory for main source set" }
        return resourcesPath.toPath()
    }
}