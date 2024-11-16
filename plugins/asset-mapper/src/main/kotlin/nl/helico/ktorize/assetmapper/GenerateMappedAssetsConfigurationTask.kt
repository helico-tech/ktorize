package nl.helico.ktorize.assetmapper

import org.gradle.api.DefaultTask
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
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

    @get:OutputFile
    val outputFile = getGeneratedDirectory().resolve("ktorize").resolve(MappedAssetsConfiguration.DEFAULT_FILE_NAME)

    @OptIn(ExperimentalPathApi::class)
    @TaskAction
    fun run() {
        logger.lifecycle("Generating static assets")

        getGeneratedDirectory().mkdirs()

        val basePath = extension.getResourcesPath()

        logger.lifecycle("Mapping all assets in $basePath")

        val results = basePath.walk().mapNotNull { path ->
            val relative = basePath.relativize(path)
            if (!path.isRegularFile()) return@mapNotNull null

            val result = assetMapperProvider.get().map(relative)

            when (result) {
                is AssetMapper.MapResult.Mapped -> result.output.copy(
                    input = result.output.input.copy(
                        path = Path("assets").resolve(result.output.input.path)
                    )
                )
                else -> null
            }
        }.toList()

        val actions = transformOutputsToActions(results)

        val assetsFile = generateAssetsFileContents(actions)
        outputFile.writeText(assetsFile)
    }

    private fun registerSourceSet() {
        val main = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME)
        main.resources.srcDir(getGeneratedDirectory())
    }

    private fun transformOutputsToActions(outputs: List<Asset.Output>): List<Action> {
        val actions = mutableListOf<Action>()

        var currentPath = Path(".")

        outputs.forEach { output ->
            val relative = output.input.path.relativeTo(currentPath)

            if (relative.parent == null) {
                actions.add(Action.DescribeAsset(output))
                return@forEach
            }

            val elements = relative.parent.toList()

            elements.forEach { element ->
                when {
                    element.endsWith("..") -> actions.add(Action.EndObject(currentPath))
                    else -> actions.add(Action.StartObject(currentPath.resolve(element)))
                }
            }

            actions.add(Action.DescribeAsset(output))

            currentPath = output.input.path.parent ?: Path(".")
        }

        return actions
    }

    private fun generateAssetsFileContents(actions: List<Action>): String {

        val folderStack = ArrayDeque<AssetTree.Folder>()

        actions.forEach { action ->
            when (action) {
                is Action.StartObject -> {
                    folderStack.addFirst(
                        AssetTree.Folder(
                            path = action.path.toString(),
                            items = emptyList()
                        )
                    )
                }
                is Action.EndObject -> {
                    val folder = folderStack.removeFirst()
                    val current = folderStack.removeFirst()

                    folderStack.addFirst(current.copy(items = current.items + folder))
                }
                is Action.DescribeAsset -> {
                    val current = folderStack.removeFirst()
                    val file = AssetTree.File(
                        logicalPath = Path("/").resolve(action.asset.input.path).toString(),
                        path = Path("/").resolve(action.asset.path).toString()
                    )
                    folderStack.addFirst(current.copy(items = current.items + file))
                }
            }
        }

        while (folderStack.size > 1) {
            val folder = folderStack.removeFirst()
            val current = folderStack.removeFirst()

            folderStack.addFirst(current.copy(items = current.items + folder))
        }

        val root = folderStack.removeFirst()

        val config = MappedAssetsConfiguration(
            basePackage = extension.assetsBasePackage.get().path,
            root = root
        )

        return config.toJSON()
    }

    sealed interface Action {
        data class StartObject(val path: Path) : Action
        data class DescribeAsset(val asset: Asset.Output) : Action
        data class EndObject(val path: Path) : Action
    }
}