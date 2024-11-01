package nl.helico.ktorize.assetmapper2.readers

import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetDigester
import java.io.Reader
import java.nio.file.Path

interface AssetReader {
    fun read(path: Path): Reader?

    fun readAsset(path: Path, digester: AssetDigester): Asset.Input? {
        val data = read(path.normalize())?.readLines() ?: return null
        return Asset.Input(path.normalize(), lines = data, digest = digester.digest(data))
    }
}

