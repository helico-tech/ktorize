package nl.helico.ktorize.assetmapper2.readers

import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileAssetReaderTests {

    @Test
    fun `read returns null when path is not found`() {
        val assetReader = FileAssetReader(Path("./src/test/resources/file-asset-reader-tests"))
        val result = assetReader.read(Path("not-found"))
        assertNull(result)
    }

    @Test
    fun `read returns reader with data when path is found`() {
        val assetReader = FileAssetReader(Path("./src/test/resources/file-asset-reader-tests"))
        val result = assetReader.read(Path("data.txt"))
        val data = result?.readText()
        assertEquals("foo", data)
    }
}