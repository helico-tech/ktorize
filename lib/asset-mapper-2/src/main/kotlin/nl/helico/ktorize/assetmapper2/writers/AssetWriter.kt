package nl.helico.ktorize.assetmapper2.writers

import nl.helico.ktorize.assetmapper2.Asset
import java.io.Writer
import java.nio.file.Path

interface AssetWriter {
    fun writer(path: Path): Writer?

    fun writeAsset(output: Asset.Output, includingDependencies: Boolean = true) {
        val writer = writer(output.path) ?: error("Invalid path ${output.path}")
        writer.use {
            output.lines.forEach { line ->
                it.appendLine(line)
            }
        }

        if (includingDependencies)  output.dependencies.forEach { writeAsset(it) }
    }
}

