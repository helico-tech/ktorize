package nl.helico.ktorize.assetmapper.readers

import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import java.io.Reader
import java.nio.file.Path

class FileAssetReader(
    private val baseDir: Path
) : AssetReader {
    override fun read(path: Path): ByteArray? {
        val file = baseDir.resolve(path)
        return if (file.toFile().exists()) file.toFile().readBytes() else null
    }
}