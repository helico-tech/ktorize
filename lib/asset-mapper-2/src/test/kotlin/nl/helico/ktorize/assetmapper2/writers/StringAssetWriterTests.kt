package nl.helico.ktorize.assetmapper2.writers

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class StringAssetWriterTests {

    @Test
    fun `write writes data to path`() {
        val stringAssetWriter = StringAssetWriter(mutableMapOf())
        val path = Path.of("path")
        val writer = stringAssetWriter.writer(path)

        writer.use {
            it.write("data")
        }

        val result = stringAssetWriter[path]
        assertEquals("data", result)
    }
}