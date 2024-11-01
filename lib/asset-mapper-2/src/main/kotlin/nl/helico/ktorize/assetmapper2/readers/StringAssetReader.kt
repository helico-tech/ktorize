package nl.helico.ktorize.assetmapper2.readers

import java.io.Reader
import java.io.StringReader
import java.nio.file.Path

class StringAssetReader(
    private val data: Map<Path, String>
) : AssetReader {
    override fun read(path: Path): Reader? {
        return data[path]?.let { return StringReader(it) }
    }
}