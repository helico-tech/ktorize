package nl.helico.ktorize.assetmapper2

import java.io.StringWriter
import java.io.Writer
import java.nio.file.Path

interface AssetWriter {
    fun writer(path: Path): Writer?
}

class StringAssetWriter(
    private val data: MutableMap<Path, String>
) : AssetWriter {

    operator fun get(path: Path): String? {
        return data[path]
    }

    override fun writer(path: Path): Writer {
        return object : Writer() {

            private val writer = StringWriter()

            override fun close() {
                data[path] = writer.toString()
            }

            override fun flush() {
                writer.flush()
            }

            override fun write(cbuf: CharArray, off: Int, len: Int) {
                writer.write(cbuf, off, len)
            }
        }
    }
}

class FileAssetWriter(
    private val baseDir: Path
) : AssetWriter {
    override fun writer(path: Path): Writer {
        return baseDir.resolve(path).toFile().writer()
    }
}