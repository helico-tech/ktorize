package nl.helico.ktorize.assetmapper2.handlers

import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetMapper
import nl.helico.ktorize.assetmapper2.readers.StringAssetReader
import nl.helico.ktorize.assetmapper2.writers.NullAssetWriter
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

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
            "no-imports.css" to Fixtures.CSS.`no-imports`
        )

        val input = mapper.reader.readAsset(Path("no-imports.css"))!!
        val output = handler.handle(input, mapper)
        assertEquals(Fixtures.CSS.`no-imports`.lines(), output.lines)
        assertEquals("no-imports.c8c1acc1b093fe24b8beb00623cd2501.css", output.path.fileName.toString())
    }

    @Test fun `external import`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "external-import.css" to Fixtures.CSS.`external-import`
        )

        val input = mapper.reader.readAsset(Path("external-import.css"))!!
        val output = handler.handle(input, mapper)
        assertEquals(Fixtures.CSS.`external-import`.lines(), output.lines)
        assertEquals("external-import.f519ba618ca8f54d57fe2bcd5f2a60b6.css", output.path.fileName.toString())
    }

    @Test fun `simple direct import`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "simple-direct-import.css" to Fixtures.CSS.`simple-direct-import`,
            "single-dependency.css" to Fixtures.CSS.`single-dependency`
        )

        val input = mapper.reader.readAsset(Path("simple-direct-import.css"))!!
        val output = handler.handle(input, mapper)

        assertEquals("@import \"single-dependency.7c79483c2157b38e21ba27f0478d8bf3.7c79483c2157b38e21ba27f0478d8bf3.css\";", output.lines[0])
        assertEquals(Fixtures.CSS.`single-dependency`.lines(), output.dependencies[0].lines)
    }

    @Test fun `basic circular import`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "simple-circular-1.css" to Fixtures.CSS.`simple-circular-1`,
            "simple-circular-2.css" to Fixtures.CSS.`simple-circular-2`
        )

        val input = mapper.reader.readAsset(Path("simple-circular-1.css"))!!
        assertFailsWith<IllegalStateException>("Circular dependency detected") {
            handler.handle(input, mapper)   
        }
    }

    private fun createMapper(handler: CSSHandler, vararg data: Pair<String, String>): AssetMapper {
        return AssetMapper(
            StringAssetReader(*data),
            NullAssetWriter,
            listOf(handler)
        )
    }
}