package nl.helico.ktorize.assetmapper2.readers

import nl.helico.ktorize.assetmapper2.Asset
import java.io.Reader
import java.nio.file.Path

interface AssetReader {
    fun read(path: Path): Reader?

    fun readAsset(path: Path): Asset.Input? {
        val data = read(path.normalize())?.readLines() ?: return null
        return Asset.Input(path.normalize(), data)
    }
}

