package nl.helico.ktorize.assetmapper2.writers

import java.io.Writer
import java.nio.file.Path

object NullAssetWriter : AssetWriter {
    override fun writer(path: Path): Writer? {
        return null
    }
}