package nl.helico.ktorize.assetmapper

import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import java.io.File
import javax.inject.Inject

abstract class AssetMapperExtension @Inject constructor() {
    companion object {
        val NAME = "assetMapper"
    }

    @get:InputDirectory
    abstract val assetDirectory: Property<File>

    init {
        assetDirectory.convention(File("src/main/resources/assets"))
    }
}