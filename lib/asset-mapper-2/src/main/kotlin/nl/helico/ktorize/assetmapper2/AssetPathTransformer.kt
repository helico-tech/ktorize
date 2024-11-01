package nl.helico.ktorize.assetmapper2

import java.nio.file.Path
import kotlin.io.path.extension

interface AssetPathTransformer {
    fun transform(input: Path, digest: String): Path

    companion object {
        operator fun invoke(): AssetPathTransformer = AssetPathTransformerImpl
    }
}

object AssetPathTransformerImpl : AssetPathTransformer {
    override fun transform(input: Path, digest: String): Path {
        val fileName = input.fileName.toString()
        val extension = input.extension
        val baseName = fileName.substring(0, fileName.length - extension.length - 1)
        return input.resolveSibling("$baseName.$digest.$extension")
    }
}