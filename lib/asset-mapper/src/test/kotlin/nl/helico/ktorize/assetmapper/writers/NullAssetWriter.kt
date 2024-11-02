package nl.helico.ktorize.assetmapper.writers

import java.io.Writer
import java.nio.file.Path

object NullAssetWriter : AssetWriter {
    override fun writer(path: Path): Writer? {
        return null
    }
}