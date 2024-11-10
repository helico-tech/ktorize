package nl.helico.ktorize.assetmapper

import org.gradle.api.DefaultTask
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Provider
import kotlin.io.path.*

abstract class GenerateStaticAssetsTask @Inject constructor(
    private val sourceSetContainer: SourceSetContainer,
    private val extension: AssetMapperExtension,
    private val assetMapperProvider: Provider<AssetMapper>,
) : DefaultTask() {

    companion object {
        val NAME = "generateStaticAssets"
        val DESCRIPTION = "Generate static assets"
    }

    init {
        group = AssetMapperPlugin.GROUP
        description = DESCRIPTION

        registerSourceSet()
    }

    private fun getGeneratedDirectory() = extension.getGeneratedDirectory().resolve("kotlin")

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
                    path = Path("/assets").resolve(result.output.path),
                    input = result.output.input.copy(
                        path = Path("assets").resolve(result.output.input.path)
                    )
                )
                else -> null
            }
        }.toList()

        val actions = transformOutputsToActions(results)

        val assetsFile = generateAssetsFileContents(actions)

        getGeneratedDirectory().resolve("Assets.kt").writeText(assetsFile)
    }

    private fun registerSourceSet() {
        val main = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME)
        (main.extensions.getByName("kotlin") as SourceDirectorySet).apply {
            srcDir(getGeneratedDirectory())
        }
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
        val builder = StringBuilder()

        builder.appendLine("// This file is generated by the AssetMapper plugin")
        builder.appendLine()

        var currentIndent = 0

        actions.forEach { action ->
            when (action) {
                is Action.StartObject -> {
                    builder.append("    ".repeat(currentIndent))
                    builder.append("object ${action.path.fileName} {")
                    currentIndent++
                }

                is Action.EndObject -> {
                    builder.append("    ".repeat(currentIndent - 1))
                    builder.append("}")
                    currentIndent--
                }

                is Action.DescribeAsset -> {
                    builder.append("    ".repeat(currentIndent))
                    builder.append("val ")
                    builder.append(generateVariableName(action.asset.input.path.fileName.toString()))
                    builder.append(" = ")
                    builder.append("\"${action.asset.path}\"")
                }
            }
            builder.append(System.lineSeparator())
        }

        while (currentIndent > 0) {
            builder.append("    ".repeat(currentIndent - 1))
            builder.append("}")
            builder.append(System.lineSeparator())
            currentIndent--
        }

        return builder.toString()
    }

    private fun generateVariableName(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9]"), "_")
    }

    sealed interface Action {
        data class StartObject(val path: Path) : Action
        data class DescribeAsset(val asset: Asset.Output) : Action
        data class EndObject(val path: Path) : Action
    }
}