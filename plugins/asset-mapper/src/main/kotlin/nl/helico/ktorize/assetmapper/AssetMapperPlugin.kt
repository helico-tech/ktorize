package nl.helico.ktorize.assetmapper

import nl.helico.ktorize.assetmapper.handlers.CSSHandler
import nl.helico.ktorize.assetmapper.readers.FileAssetReader
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources
import java.nio.charset.Charset
import javax.inject.Provider
import kotlin.io.path.name

abstract class AssetMapperPlugin : Plugin<Project> {

    companion object {
        val GROUP = "ktorize"
    }

    override fun apply(target: Project): Unit = with(target) {
        val extension = extensions.create(
            AssetMapperExtension.NAME,
            AssetMapperExtension::class.java
        )

        val assetMapperProvider : Provider<AssetMapper> =
            object : Provider<AssetMapper> {
                private val mapper by lazy {
                    logger.lifecycle("Instantiating asset mapper")
                    AssetMapper(
                        reader = FileAssetReader(extension.getResourcesPath()),
                        handlers = listOf(CSSHandler())
                    )
                }

                override fun get(): AssetMapper = mapper
            }

        val generateMappedAssetsConfigurationTask = target.tasks.register(
            GenerateMappedAssetsConfigurationTask.NAME,
            GenerateMappedAssetsConfigurationTask::class.java,
            extension,
            assetMapperProvider
        )

        project.tasks.getByName("compileKotlin").dependsOn(generateMappedAssetsConfigurationTask)

        project.tasks.withType(ProcessResources::class.java) { processResources ->
            processResources.dependsOn(generateMappedAssetsConfigurationTask)

            processResources.filesMatching("${extension.assetsBasePackage.get()}/**/*") { fileCopyDetails ->
                val assetMapper = assetMapperProvider.get()

                val output = assetMapper.map(fileCopyDetails.file.toPath())
                if (output !is AssetMapper.MapResult.Mapped) return@filesMatching

                fileCopyDetails.name = output.output.path.name

                if (output.output.input.digest == output.output.digest) {
                    return@filesMatching
                }

                val contents = output.output.source.toString(Charset.defaultCharset())
                val lines = contents.lines()
                var lineNo = 0

                fileCopyDetails.filter {
                    lines[lineNo++]
                }
            }
        }
    }
}