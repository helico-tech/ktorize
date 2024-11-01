package nl.helico.ktorize.assetmapper2.readers

import java.io.Reader
import java.io.StringReader
import java.nio.file.Path
import kotlin.io.path.Path

class StringAssetReader(
    private val data: Map<Path, String>
) : AssetReader {
    constructor(vararg data: Pair<String, String>) : this(data.map { Path(it.first) to it.second }.toMap())

    override fun read(path: Path): Reader? {
        return data[path]?.let { return StringReader(it) }
    }
}