package nl.helico.ktorize.assetmapper2

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultHandlerTests {

    @Test
    fun `accepts returns true`() {
        val handler = DefaultHandler()
        val input = Asset.Input(Path.of("test"), listOf("data"))
        val result = handler.accepts(input)
        assertTrue(result)
    }

    @Test
    fun `handle returns output with transformed path`() {
        val data = listOf("data")
        val path = Path.of("/some/dir/test.txt")

        val handler = DefaultHandler()
        val input = Asset.Input(path, data)
        val digester = MD5AssetDigester
        val pathTransformer = AssetPathTransformer()
        val output = handler.handle(input, digester, pathTransformer)

        assertEquals(Path.of("/some/dir/test.8d777f385d3dfec8815d20f7496026dc.txt"), output.path)
    }

    @Test
    fun `handle returns output with transformed path without extension`() {
        val data = listOf("data")
        val path = Path.of("/some/dir/test")

        val handler = DefaultHandler()
        val input = Asset.Input(path, data)
        val digester = MD5AssetDigester
        val pathTransformer = AssetPathTransformer()
        val output = handler.handle(input, digester, pathTransformer)

        assertEquals(Path.of("/some/dir/test.8d777f385d3dfec8815d20f7496026dc"), output.path)
    }
}