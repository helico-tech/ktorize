package nl.helico.ktorize.importmap

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportMapTests {

    @Test
    fun serialize() {
        val importMap = ImportMap {
            addModuleSpecifier("@hotwired/stimulus", "https://cdn.skypack.dev/@hotwired/stimulus")
        }

        val serialized = Json.encodeToString(importMap)

        assertEquals("""{"imports":{"@hotwired/stimulus":"https://cdn.skypack.dev/@hotwired/stimulus"}}""", serialized)
    }
}