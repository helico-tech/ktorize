package nl.helico.ktorize.importmap

import io.ktor.server.application.*
import io.ktor.util.logging.*
import nl.helico.ktorize.html.renderContext

internal val name = "ImportMapPlugin"

internal val LOGGER = KtorSimpleLogger(name)

val ImportMapPlugin = createRouteScopedPlugin(name) {

    onCall { call ->
        val builder = ImportMapBuilderImpl()

        call.attributes.put(ImportMapBuilder.Key, builder)
        LOGGER.debug("Registering ImportMapBuilder to call attributes with key: {}", ImportMapBuilder.Key)

        val pass = AddImportMapRenderPass(builder)

        call.renderContext.addRenderPass(pass)
    }
}