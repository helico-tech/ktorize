package nl.helico.ktorize.assetmapper.readers

import java.io.Reader
import java.nio.file.Path

class ResourceAssetReader(
    val basePackage: Path,
    val classLoader: ClassLoader
) : AssetReader {
    override fun read(path: Path): Reader? {
        val stream = classLoader.getResourceAsStream(basePackage.resolve(path).toString()) ?: return null
        return stream.reader()
    }
}