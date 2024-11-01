package nl.helico.ktorize.assetmapper2.writers

import java.io.Writer
import java.nio.file.Path

interface AssetWriter {
    fun writer(path: Path): Writer?
}

