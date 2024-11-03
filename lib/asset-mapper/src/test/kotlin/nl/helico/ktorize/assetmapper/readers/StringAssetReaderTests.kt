package nl.helico.ktorize.assetmapper.readers

import io.ktor.utils.io.*
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringAssetReaderTests {

    @Test
    fun `read returns null when path is not found`() {
        val reader = StringAssetReader(emptyMap())
        val result = reader.read(Path("not-found"))
        assertNull(result)
    }

    @Test
    fun `read returns reader with data when path is found`() {
        val reader = StringAssetReader(mapOf(Path("found") to "data"))
        val result = reader.read(Path("found"))
        val data = result?.readText()
        assertEquals("data", data)
    }
}