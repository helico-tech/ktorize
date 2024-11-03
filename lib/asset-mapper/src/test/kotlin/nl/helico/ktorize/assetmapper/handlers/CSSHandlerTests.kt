package nl.helico.ktorize.assetmapper.handlers

import io.ktor.utils.io.*
import kotlinx.io.Buffer
import nl.helico.ktorize.assetmapper.Asset
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.readers.StringAssetReader
import kotlin.io.path.Path
import kotlin.test.*

class CSSHandlerTests {

    @Test
    fun `test accepts`() {
        val handler = CSSHandler()
        val input = Asset.Input(path = Path("test.css"), source = Buffer(), digest = "123")
        assert(handler.accepts(input))
    }

    @Test
    fun `no imports`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "no-imports.css" to Fixtures.CSS.`no-imports`
        )

        val input = mapper.read(Path("no-imports.css"))!!
        val output = handler.handle(input, mapper)
        assertEquals(Fixtures.CSS.`no-imports`, output.source.readText())
        assertEquals("no-imports.c8c1acc1b093fe24b8beb00623cd2501.css", output.path.fileName.toString())
    }

    @Test fun `external import`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "external-import.css" to Fixtures.CSS.`external-import`
        )

        val input = mapper.read(Path("external-import.css"))!!
        val output = handler.handle(input, mapper)
        assertEquals(Fixtures.CSS.`external-import`, output.source.readText())
        assertEquals("external-import.f519ba618ca8f54d57fe2bcd5f2a60b6.css", output.path.fileName.toString())
    }

    @Test fun `simple direct import`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "simple-direct-import.css" to Fixtures.CSS.`simple-direct-import`,
            "single-dependency.css" to Fixtures.CSS.`single-dependency`
        )

        val inputAsset = mapper.read(Path("simple-direct-import.css"))!!
        val outputAsset = handler.handle(inputAsset, mapper)

        assertNotEquals(inputAsset.digest, outputAsset.digest)
        assertEquals("@import \"single-dependency.7c79483c2157b38e21ba27f0478d8bf3.css\";", outputAsset.source.readText().lines()[0])
        assertEquals(Fixtures.CSS.`single-dependency`, outputAsset.dependencies[0].source.readText())
    }

    @Test fun `simple direct import in subdirectory`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "/assets/css/simple-direct-import.css" to Fixtures.CSS.`simple-direct-import`,
            "/assets/css/single-dependency.css" to Fixtures.CSS.`single-dependency`
        )

        val inputAsset = mapper.read(Path("/assets/css/simple-direct-import.css"))!!
        val outputAsset = handler.handle(inputAsset, mapper)

        assertNotEquals(inputAsset.digest, outputAsset.digest)
        assertEquals("@import \"single-dependency.7c79483c2157b38e21ba27f0478d8bf3.css\";", outputAsset.source.readText().lines()[0])
        assertEquals(Fixtures.CSS.`single-dependency`, outputAsset.dependencies[0].source.readText())
    }

    @Test fun `simple direct import in other directory`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "/assets/css/main.css" to Fixtures.CSS.`dependency-other-dir1`,
            "/assets/dependency.css" to Fixtures.CSS.`dependency-other-dir2`
        )

        val inputAsset = mapper.read(Path("/assets/css/main.css"))!!
        val outputAsset = handler.handle(inputAsset, mapper)

        assertNotEquals(inputAsset.digest, outputAsset.digest)
        assertEquals("@import \"../dependency.d41d8cd98f00b204e9800998ecf8427e.css\";", outputAsset.source.readText().lines()[0])
    }

    @Test fun `basic circular import`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "simple-circular-1.css" to Fixtures.CSS.`simple-circular-1`,
            "simple-circular-2.css" to Fixtures.CSS.`simple-circular-2`
        )

        val input = mapper.read(Path("simple-circular-1.css"))!!
        assertFailsWith<IllegalStateException>("Circular dependency detected") {
            handler.handle(input, mapper)
        }
    }

    @Test fun `multi-level-circular`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "multi-level-circular-1.css" to Fixtures.CSS.`multi-level-circular-1`,
            "multi-level-circular-2.css" to Fixtures.CSS.`multi-level-circular-2`,
            "multi-level-circular-3.css" to Fixtures.CSS.`multi-level-circular-3`
        )

        val input = mapper.read(Path("multi-level-circular-1.css"))!!
        assertFailsWith<IllegalStateException>("Circular dependency detected") {
            handler.handle(input, mapper)
        }
    }

    @Test fun `multi level dependency`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "multi-level-dependency-1.css" to Fixtures.CSS.`multi-level-dependency-1`,
            "multi-level-dependency-2.css" to Fixtures.CSS.`multi-level-dependency-2`,
            "multi-level-dependency-3.css" to Fixtures.CSS.`multi-level-dependency-3`,
            "multi-level-dependency-4.css" to Fixtures.CSS.`multi-level-dependency-4`
        )

        val input = mapper.read(Path("multi-level-dependency-1.css"))!!
        val output = handler.handle(input, mapper)

        assertEquals(2, output.dependencies.size)
        assertEquals(2, output.dependencies[0].dependencies.size)
        assertEquals(0, output.dependencies[1].dependencies.size)
        assertEquals(1, output.dependencies[0].dependencies[0].dependencies.size)
        assertEquals(0, output.dependencies[0].dependencies[1].dependencies.size)
    }

    @Test fun `other url`() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "other-urls.css" to Fixtures.CSS.`other-urls`,
            "container.png" to Fixtures.CSS.`container-png`
        )

        val mapped = mapper.map(Path("other-urls.css"))
        assertIs<AssetMapper.MapResult.Mapped>(mapped)
        assertEquals(1, mapped.output.dependencies.size)
    }

    @Test fun missing() {
        val handler = CSSHandler()
        val mapper = createMapper(handler,
            "missing-url.css" to Fixtures.CSS.`missing-url`
        )

        val result = mapper.map(Path("missing-url.css"))
        assertIs<AssetMapper.MapResult.Error>(result)
    }

    private fun createMapper(handler: CSSHandler, vararg data: Pair<String, String>): AssetMapper {
        return AssetMapper(
            StringAssetReader(*data),
            listOf(handler)
        )
    }
}