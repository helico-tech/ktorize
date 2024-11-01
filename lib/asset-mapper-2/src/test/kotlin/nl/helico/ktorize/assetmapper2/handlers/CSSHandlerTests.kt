package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetMapper
import nl.helico.ktorize.assetmapper2.readers.StringAssetReader
import nl.helico.ktorize.assetmapper2.writers.NullAssetWriter
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class CSSHandlerTests {

    @Test
    fun `test accepts`() {
        val handler = CSSHandler()
        val input = Asset.Input(path = Path("test.css"), lines = emptyList())
        assert(handler.accepts(input))
    }

    @Test
    fun `no imports`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "no-imports.css" to Fixtures.CSS.`no imports`
        )
        val input = mapper.reader.readAsset(Path("no-imports.css"))!!
        val output = handler.handle(input, mapper)
        assertEquals(Fixtures.CSS.`no imports`.lines(), output.lines)
        assertEquals("no-imports.c8c1acc1b093fe24b8beb00623cd2501.css", output.path.fileName.toString())
    }

    private fun createMapper(handler: CSSHandler, vararg data: Pair<String, String>): AssetMapper {
        return AssetMapper(
            StringAssetReader(*data),
            NullAssetWriter,
            listOf(handler)
        )
    }
}