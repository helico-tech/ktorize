package nl.helico.ktorize.assetmapper

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class AssetMapperPlugin : Plugin<Project> {

    companion object {
        val GROUP = "ktorize"
    }

    override fun apply(target: Project): Unit = with(target) {
        val extension = extensions.create(
            AssetMapperExtension.NAME,
            AssetMapperExtension::class.java
        )

        val mapAssetsTask = target.tasks.register(
            MapAssetsTask.NAME,
            MapAssetsTask::class.java,
            extension
        )

        mapAssetsTask.get().dependsOn("processResources")
        project.tasks.getByName("build").dependsOn(mapAssetsTask)
    }
}