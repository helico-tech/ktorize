package nl.helico.ktorize.assetmapper.readers

import kotlinx.io.Source
import java.io.Reader
import java.nio.file.Path

object NullAssetReader : AssetReader {
    override fun read(path: Path): Source? {
        return null
    }
}