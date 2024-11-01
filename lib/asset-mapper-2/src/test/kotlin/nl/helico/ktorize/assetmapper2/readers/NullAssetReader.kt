package nl.helico.ktorize.assetmapper2.readers

import java.io.Reader
import java.nio.file.Path

object NullAssetReader : AssetReader {
    override fun read(path: Path): Reader? {
        return null
    }
}