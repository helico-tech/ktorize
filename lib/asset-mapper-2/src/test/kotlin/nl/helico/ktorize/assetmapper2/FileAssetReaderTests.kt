package nl.helico.ktorize.assetmapper2

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileAssetReaderTests {

    @Test
    fun `read returns null when path is not found`() {
        val assetReader = FileAssetReader(Path.of("./src/test/resources/file-asset-reader-tests"))
        val result = assetReader.read(Path.of("not-found"))
        assertNull(result)
    }

    @Test
    fun `read returns reader with data when path is found`() {
        val assetReader = FileAssetReader(Path.of("./src/test/resources/file-asset-reader-tests"))
        val result = assetReader.read(Path.of("data.txt"))
        val data = result?.readText()
        assertEquals("foo", data)
    }
}