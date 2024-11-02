package nl.helico.ktorize.assetmapper.readers

import java.io.Reader
import java.nio.file.Path

class FileAssetReader(
    private val baseDir: Path
) : AssetReader {
    override fun read(path: Path): Reader? {
        val file = baseDir.resolve(path)
        return if (file.toFile().exists()) file.toFile().reader() else null
    }
}