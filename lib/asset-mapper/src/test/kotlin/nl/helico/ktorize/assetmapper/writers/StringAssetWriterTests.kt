package nl.helico.ktorize.assetmapper.writers

import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class StringAssetWriterTests {

    @Test
    fun `write writes data to path`() {
        val stringAssetWriter = StringAssetWriter(mutableMapOf())
        val path = Path("path")
        val writer = stringAssetWriter.writer(path)

        writer.use {
            it.write("data")
        }

        val result = stringAssetWriter[path]
        assertEquals("data", result)
    }
}