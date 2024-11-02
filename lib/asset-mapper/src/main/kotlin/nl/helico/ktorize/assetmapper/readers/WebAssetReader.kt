package nl.helico.ktorize.assetmapper.readers

import kotlinx.io.Source
import java.io.Reader
import java.nio.file.Path
import kotlin.io.path.relativeTo

class WebAssetReader(
    val basePath: Path,
    val downstream: AssetReader
) : AssetReader {
    override fun read(path: Path): Source? {
        if (path.startsWith(basePath)) {
            val relative = path.relativeTo(basePath)
            return downstream.read(relative)
        }
        return null
    }
}