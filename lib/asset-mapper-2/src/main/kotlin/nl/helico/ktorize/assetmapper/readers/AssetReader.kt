package nl.helico.ktorize.assetmapper.readers

import nl.helico.ktorize.assetmapper.Asset
import nl.helico.ktorize.assetmapper.AssetDigester
import java.io.Reader
import java.nio.file.Path

interface AssetReader {
    fun read(path: Path): Reader?

    fun readAsset(path: Path, digester: AssetDigester): Asset.Input? {
        val content = read(path.normalize())?.readText() ?: return null
        return Asset.Input(path.normalize(), content = content, digest = digester.digest(content))
    }
}

