package nl.helico.ktorize.assetmapper2.readers

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringAssetReaderTests {

    @Test
    fun `read returns null when path is not found`() {
        val reader = StringAssetReader(emptyMap())
        val result = reader.read(Path.of("not-found"))
        assertNull(result)
    }

    @Test
    fun `read returns reader with data when path is found`() {
        val reader = StringAssetReader(mapOf(Path.of("found") to "data"))
        val result = reader.read(Path.of("found"))
        val data = result?.readText()
        assertEquals("data", data)
    }
}