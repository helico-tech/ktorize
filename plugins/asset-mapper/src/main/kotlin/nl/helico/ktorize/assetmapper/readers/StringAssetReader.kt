package nl.helico.ktorize.assetmapper.readers

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.writeString
import java.io.Reader
import java.io.StringReader
import java.nio.file.Path
import kotlin.io.path.Path

class StringAssetReader(
    private val data: Map<Path, String>
) : AssetReader {
    constructor(vararg data: Pair<String, String>) : this(data.map { Path(it.first) to it.second }.toMap())

    override fun read(path: Path): ByteArray? {
        return data[path.normalize()]?.encodeToByteArray()
    }
}