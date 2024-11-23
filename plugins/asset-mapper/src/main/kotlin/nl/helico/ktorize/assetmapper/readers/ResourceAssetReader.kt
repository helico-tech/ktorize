package nl.helico.ktorize.assetmapper.readers

import io.ktor.utils.io.core.*
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import java.io.Reader
import java.nio.file.Path

class ResourceAssetReader(
    val basePackage: Path,
    val classLoader: ClassLoader
) : AssetReader {
    override fun read(path: Path): ByteArray? {
        val stream = classLoader.getResourceAsStream(basePackage.resolve(path).toString()) ?: return null
        return stream.readAllBytes()
    }
}