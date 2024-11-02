package nl.helico.ktorize.assetmapper.writers

import java.io.StringWriter
import java.io.Writer
import java.nio.file.Path

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