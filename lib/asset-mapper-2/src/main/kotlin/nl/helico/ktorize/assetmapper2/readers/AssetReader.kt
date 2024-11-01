package nl.helico.ktorize.assetmapper2.readers

import java.io.Reader
import java.nio.file.Path

interface AssetReader {
    fun read(path: Path): Reader?
}

