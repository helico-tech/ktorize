package nl.helico.ktorize.assetmapper

import java.nio.file.Path
import kotlin.io.path.extension

interface AssetPathTransformer {
    fun createMappedAssetRegex(basePath: Path): Regex

    fun transform(input: Path, digest: String): Path

    companion object {
        operator fun invoke(): AssetPathTransformer = AssetPathTransformerImpl
    }
}

object AssetPathTransformerImpl : AssetPathTransformer {

    override fun createMappedAssetRegex(basePath: Path): Regex {
        return Regex("""${basePath.normalize()}(?<directoryName>.*?)/(?<baseName>[^/]+).(?<digest>[0-9a-f]{32})\.(?<extension>[^/]+)""")
    }

    override fun transform(input: Path, digest: String): Path {
        val fileName = input.fileName.toString()
        val extension = input.extension
        val baseName = when {
            extension.isEmpty() -> fileName
            else -> fileName.removeSuffix(".$extension")
        }

        val parts = listOf(baseName, digest, extension).filterNot { it.isEmpty() }
        return input.resolveSibling(parts.joinToString("."))
    }
}