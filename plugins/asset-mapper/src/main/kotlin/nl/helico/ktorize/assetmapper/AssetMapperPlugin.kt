package nl.helico.ktorize.assetmapper

import nl.helico.ktorize.assetmapper.handlers.CSSHandler
import nl.helico.ktorize.assetmapper.readers.FileAssetReader
import org.gradle.api.Plugin
import org.gradle.api.Project
import javax.inject.Provider

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

        val mapAssetsTask = target.tasks.register(
            MapAssetsTask.NAME,
            MapAssetsTask::class.java,
            extension,
            assetMapperProvider
        )

        val generateStaticAssetsTask = target.tasks.register(
            GenerateStaticAssetsTask.NAME,
            GenerateStaticAssetsTask::class.java,
            extension,
            assetMapperProvider
        )

        project.tasks.getByName("compileKotlin").dependsOn(generateStaticAssetsTask)
        project.tasks.getByName("compileKotlin").dependsOn(mapAssetsTask)
        project.tasks.getByName("processResources").dependsOn(mapAssetsTask)
    }
}