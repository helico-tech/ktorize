package nl.helico.ktorize.importmap

import io.ktor.server.application.*
import nl.helico.ktorize.html.hooks

internal val name = "ImportMapPlugin"

val ImportMapPlugin = createApplicationPlugin(name) {

    onCall { call ->
        val builder = ImportMapBuilderImpl()
        call.attributes.put(ImportMapBuilder.Key, builder)
        call.hooks.add(AddImportMapHook(builder))
    }
}