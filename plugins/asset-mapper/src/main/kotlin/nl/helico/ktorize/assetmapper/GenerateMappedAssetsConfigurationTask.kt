package nl.helico.ktorize.assetmapper

import org.gradle.api.DefaultTask
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Provider
import kotlin.io.path.*

abstract class GenerateMappedAssetsConfigurationTask @Inject constructor(
    private val sourceSetContainer: SourceSetContainer,
    private val extension: AssetMapperExtension,
    private val assetMapperProvider: Provider<AssetMapper>,
) : DefaultTask() {

    companion object {
        val NAME = "generateMappedAssetsConfiguration"
        val DESCRIPTION = "Generate configuration file for mapped assets"
    }

    init {
        group = AssetMapperPlugin.GROUP
        description = DESCRIPTION

        registerSourceSet()
    }

    private fun getGeneratedDirectory() = extension.getGeneratedDirectory().resolve("resources")

    @get:InputDirectory
    abstract val assetDirectory: Property<File>

    @get:OutputFile
    abstract val outputFile: Property<File>

    init {
        assetDirectory.convention(extension.getResourcesPath().toFile())
        outputFile.convention(getGeneratedDirectory().resolve("ktorize").resolve(MappedAssetsConfiguration.DEFAULT_FILE_NAME))
    }


    @OptIn(ExperimentalPathApi::class)
    @TaskAction
    fun run() {
        logger.lifecycle("Generating static assets")

        getGeneratedDirectory().mkdirs()

        val basePath = extension.getResourcesPath().toFile()

        val folder = handleFolder(basePath)

        val configuration = MappedAssetsConfiguration(
            basePackage = extension.assetsBasePackage.get().name,
            root = folder
        )

        outputFile.get().writeText(configuration.toJSON())
    }

    private fun handleFolder(file: File): AssetTree.Folder {
        require(file.isDirectory) { "Expected a directory, got a file" }

        val folder = createAssetTreeFolder(file)

        val items = file.listFiles().map { item ->
            if (item.isDirectory) {
                handleFolder(item)
            } else {
                handleFile(item)
            }
        }

        return folder.copy(items = items)
    }

    private fun handleFile(file: File): AssetTree.File {
        require(file.isFile) { "Expected a file, got a directory" }

        return createAssetTreeFile(file)
    }

    private fun createAssetTreeFolder(file: File): AssetTree.Folder {
        require(file.isDirectory) { "Expected a directory, got a file" }

        val path = getRelativePath(file)

        return AssetTree.Folder(
            logicalPath = path.toString(),
            path = path.toString(),
            items = emptyList()
        )
    }

    private fun createAssetTreeFile(file: File): AssetTree.File {
        require(file.isFile) { "Expected a file, got a directory" }

        val mapper = assetMapperProvider.get()

        val result = mapper.map(file.toPath())

        if (result !is AssetMapper.MapResult.Mapped) {
            throw IllegalStateException("Failed to map asset: $file")
        }

        val output = result.output

        return AssetTree.File(
            logicalPath = getRelativePath(output.input.path.toFile()).toString(),
            path = getRelativePath(output.path.toFile()).toString(),
        )
    }

    private fun getRelativePath(file: File): Path {
        return Path("/", assetDirectory.get().name, file.relativeTo(assetDirectory.get()).path).normalize()
    }

    private fun registerSourceSet() {
        val main = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME)
        main.resources.srcDir(getGeneratedDirectory())
    }
}