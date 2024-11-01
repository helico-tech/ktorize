package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetMapper
import nl.helico.ktorize.assetmapper2.AssetPathTransformer
import nl.helico.ktorize.assetmapper2.MD5AssetDigester
import nl.helico.ktorize.assetmapper2.readers.NullAssetReader
import nl.helico.ktorize.assetmapper2.writers.NullAssetWriter
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultHandlerTests {

    @Test
    fun `accepts returns true`() {
        val handler = DefaultHandler()
        val input = Asset.Input(Path("test"), listOf("data"), "123")
        val result = handler.accepts(input)
        assertTrue(result)
    }

    @Test
    fun `handle returns output with transformed path`() {
        val data = listOf("data")
        val path = Path("/some/dir/test.txt")

        val handler = DefaultHandler()
        val input = Asset.Input(path, data, "123")

        val digester = MD5AssetDigester
        val pathTransformer = AssetPathTransformer()
        val mapper = AssetMapper(NullAssetReader, NullAssetWriter, listOf(), digester, pathTransformer)

        val output = handler.handle(input, mapper)

        assertEquals(Path("/some/dir/test.123.txt"), output.path)
    }

    @Test
    fun `handle returns output with transformed path without extension`() {
        val data = listOf("data")
        val path = Path("/some/dir/test")

        val handler = DefaultHandler()
        val input = Asset.Input(path, data, "123")
        val digester = MD5AssetDigester
        val pathTransformer = AssetPathTransformer()
        val mapper = AssetMapper(NullAssetReader, NullAssetWriter, listOf(), digester, pathTransformer)

        val output = handler.handle(input, mapper)

        assertEquals(Path("/some/dir/test.123"), output.path)
    }
}