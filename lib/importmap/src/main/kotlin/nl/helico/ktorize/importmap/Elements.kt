package nl.helico.ktorize.importmap

import kotlinx.html.HEAD
import kotlinx.html.HtmlTagMarker
import kotlinx.html.script
import kotlinx.html.unsafe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@HtmlTagMarker
fun HEAD.ImportMap(importMap: ImportMap) {
    script(type = "importmap") {
        unsafe { +importMap.toString() }
    }
}

@HtmlTagMarker
fun HEAD.ImportMap(builder: ImportMapBuilder.() -> Unit) {
    ImportMap(ImportMapBuilderImpl().apply(builder).build())
}