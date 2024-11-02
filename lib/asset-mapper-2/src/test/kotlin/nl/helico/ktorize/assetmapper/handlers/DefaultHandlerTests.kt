package nl.helico.ktorize.assetmapper.handlers

import nl.helico.ktorize.assetmapper.Asset
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.AssetPathTransformer
import nl.helico.ktorize.assetmapper.MD5AssetDigester
import nl.helico.ktorize.assetmapper.readers.NullAssetReader
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultHandlerTests {

    @Test
    fun `accepts returns true`() {
        val handler = DefaultHandler()
        val input = Asset.Input(Path("test"), "data", "123")
        val result = handler.accepts(input)
        assertTrue(result)
    }

    @Test
    fun `handle returns output with transformed path`() {
        val data = "data"
        val path = Path("/some/dir/test.txt")

        val handler = DefaultHandler()
        val input = Asset.Input(path, data, "123")

        val digester = MD5AssetDigester
        val pathTransformer = AssetPathTransformer()
        val mapper = AssetMapper(NullAssetReader, listOf(), digester, pathTransformer)

        val output = handler.handle(input, mapper)

        assertEquals(Path("/some/dir/test.123.txt"), output.path)
    }

    @Test
    fun `handle returns output with transformed path without extension`() {
        val data = "data"
        val path = Path("/some/dir/test")

        val handler = DefaultHandler()
        val input = Asset.Input(path, data, "123")
        val digester = MD5AssetDigester
        val pathTransformer = AssetPathTransformer()
        val mapper = AssetMapper(NullAssetReader, listOf(), digester, pathTransformer)

        val output = handler.handle(input, mapper)

        assertEquals(Path("/some/dir/test.123"), output.path)
    }
}