package nl.helico.ktorize.assetmapper2

import java.io.Reader
import java.io.StringReader
import java.nio.file.Path

interface AssetReader {
    fun read(path: Path): Reader?
}

class StringAssetReader(
    private val data: Map<Path, String>
) : AssetReader {
    override fun read(path: Path): Reader? {
        return data[path]?.let { return StringReader(it) }
    }
}