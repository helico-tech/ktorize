package nl.helico.ktorize.assetmapper2.writers

import java.io.Writer
import java.nio.file.Path

class FileAssetWriter(
    private val baseDir: Path
) : AssetWriter {
    override fun writer(path: Path): Writer {
        return baseDir.resolve(path).toFile().writer()
    }
}