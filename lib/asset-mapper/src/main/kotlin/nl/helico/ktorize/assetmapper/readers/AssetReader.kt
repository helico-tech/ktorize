package nl.helico.ktorize.assetmapper.readers

import io.ktor.utils.io.core.*
import kotlinx.io.Source
import nl.helico.ktorize.assetmapper.Asset
import nl.helico.ktorize.assetmapper.AssetDigester
import java.nio.file.Path

interface AssetReader {
    fun read(path: Path): Source?

    fun readAsset(path: Path, digester: AssetDigester): Asset.Input? {
        val source = read(path.normalize()) ?: return null
        return Asset.Input(path.normalize(), source = source, digest = digester.digest(source))
    }
}

